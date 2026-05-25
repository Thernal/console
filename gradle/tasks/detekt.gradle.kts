import org.w3c.dom.Element

val detektTodoRegex = Regex(
    pattern = """[ \t]*//[ \t]*TODO[ \t]*:?[ \t]*[Dd]etekt\b.*$""",
    options = setOf(RegexOption.MULTILINE),
)

fun java.io.File.isIgnoredDetektPath(): Boolean {
    val normalized = this.invariantSeparatorsPath
    return normalized.contains("/build/") ||
        normalized.contains("/.gradle/") ||
        normalized.contains("/.git/")
}

tasks.register("detektClearTodos") {
    description = "Removes all '// TODO: Detekt [...]' annotations from Kotlin source files."
    group = "verification"
    notCompatibleWithConfigurationCache("Reads and writes source files directly.")

    doLast {
        val projectDir = layout.projectDirectory.asFile
        val changedProp = project.findProperty("detektChangedFiles")?.toString()

        val cleanedCount = if (!changedProp.isNullOrBlank()) {
            val targetFiles = changedProp.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() && it.endsWith(".kt") }
                .map { java.io.File(it) }
                .filter { it.exists() }
            clearDetektTodoAnnotationsInFiles(targetFiles, detektTodoRegex)
        } else {
            clearDetektTodoAnnotations(projectDir, detektTodoRegex)
        }

        logger.lifecycle("  Removed Detekt TODO annotations from $cleanedCount file(s).")
    }
}

tasks.register("detektMergeDelta") {
    description = "Merges the per-commit delta XML report into the main detekt report."
    group = "verification"
    notCompatibleWithConfigurationCache("Reads and writes report files.")
    mustRunAfter("detektChanged")
    mustRunAfter("detekt")

    doLast {
        val projectDir = layout.projectDirectory.asFile
        val mainXml = java.io.File(projectDir, ".misc/detekt/detekt-report.xml")
        val deltaXml = java.io.File(projectDir, ".misc/detekt/detekt-report-delta.xml")
        fun canonicalPath(rawPath: String): String {
            val candidate = java.io.File(rawPath)
            val resolved = if (candidate.isAbsolute) candidate else java.io.File(projectDir, rawPath)
            return resolved.canonicalFile.absolutePath
        }

        fun parseCsvPaths(csv: String?): Set<String> {
            if (csv.isNullOrBlank()) return emptySet()
            return csv.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map(::canonicalPath)
                .toSet()
        }

        val changedProp = project.findProperty("detektChangedFiles")?.toString()
        val staleProp = project.findProperty("detektStaleReportFiles")?.toString()
        val stalePaths = parseCsvPaths(staleProp)

        if (!deltaXml.exists() && stalePaths.isEmpty()) {
            logger.lifecycle("  No delta report and no stale paths, skipping merge.")
            return@doLast
        }

        val deltaContent = if (deltaXml.exists()) deltaXml.readText(Charsets.UTF_8) else ""

        val scannedPaths: Set<String> = if (!changedProp.isNullOrBlank()) {
            parseCsvPaths(changedProp)
        } else if (deltaXml.exists()) {
            Regex("""<file name="([^"]+)"""")
                .findAll(deltaContent)
                .map { canonicalPath(it.groupValues[1]) }
                .toSet()
        } else {
            emptySet()
        }

        val fileBlockRegex = Regex("""<file name="([^"]+)"[^>]*>[\s\S]*?</file>""")
        val deltaEntries = mutableMapOf<String, String>()
        if (deltaXml.exists()) {
            fileBlockRegex.findAll(deltaContent).forEach { match ->
                deltaEntries[canonicalPath(match.groupValues[1])] = match.value
            }
        }

        if (!mainXml.exists()) {
            if (deltaEntries.isEmpty()) {
                if (deltaXml.exists()) deltaXml.delete()
                if (stalePaths.isNotEmpty()) {
                    logger.lifecycle("  Stale paths provided but main report is missing, nothing to purge.")
                }
                return@doLast
            }
            mainXml.writeText(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<checkstyle version=\"4.3\">\n" +
                    deltaEntries.values.joinToString("\n") + "\n</checkstyle>\n",
                Charsets.UTF_8,
            )
            if (deltaXml.exists()) deltaXml.delete()
            logger.lifecycle("  Created main report with ${deltaEntries.size} file entry(ies).")
            return@doLast
        }

        val mainContent = mainXml.readText(Charsets.UTF_8)
        val keptEntries = linkedMapOf<String, String>()
        var purgedStaleCount = 0

        fileBlockRegex.findAll(mainContent).forEach { match ->
            val canonical = canonicalPath(match.groupValues[1])
            if (canonical in stalePaths) {
                purgedStaleCount++
                return@forEach
            }
            if (canonical in scannedPaths) return@forEach
            keptEntries[canonical] = match.value
        }

        deltaEntries.forEach { (canonical, block) -> keptEntries[canonical] = block }

        mainXml.writeText(
            buildString {
                append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<checkstyle version=\"4.3\">\n")
                if (keptEntries.isNotEmpty()) {
                    append(keptEntries.values.joinToString("\n"))
                    append('\n')
                }
                append("</checkstyle>\n")
            },
            Charsets.UTF_8,
        )
        if (deltaXml.exists()) deltaXml.delete()
        logger.lifecycle(
            "  Merged ${deltaEntries.size} violation entry(ies) into main report " +
                "(cleared ${scannedPaths.size} scanned path(s), purged $purgedStaleCount deleted/renamed path(s)).",
        )
    }
}

tasks.register("detektAnnotate") {
    description = "Appends detekt violations as TODO comments at the end of offending lines."
    group = "verification"
    notCompatibleWithConfigurationCache("Reads and writes source files directly.")
    dependsOn("detektClearTodos")
    mustRunAfter("detekt")
    mustRunAfter("detektMergeDelta")
    mustRunAfter("detektChangedFormat")
    mustRunAfter("detektFormat")

    doLast {
        val marker = "// TODO: Detekt"
        val xmlReport = layout.projectDirectory.file(".misc/detekt/detekt-report.xml").asFile
        val projectDir = layout.projectDirectory.asFile
        val enableCustomRules = project.findProperty("detektAnnotateCustomRules")
            ?.toString()
            ?.equals("true", ignoreCase = true)
            ?: true

        if (!xmlReport.exists()) {
            logger.lifecycle("  No XML report found, skipping annotation.")
            return@doLast
        }

        val changedProp = project.findProperty("detektChangedFiles")?.toString()
        val changedFiles: Set<java.io.File>? = if (!changedProp.isNullOrBlank()) {
            changedProp.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() && it.endsWith(".kt") }
                .map { java.io.File(it).canonicalFile }
                .filter { it.exists() }
                .toSet()
        } else {
            null
        }
        val changedPaths: Set<String>? = changedFiles?.map { it.absolutePath }?.toSet()

        fun canonicalPath(rawPath: String): String {
            val candidate = java.io.File(rawPath)
            val resolved = if (candidate.isAbsolute) candidate else java.io.File(projectDir, rawPath)
            return resolved.canonicalFile.absolutePath
        }

        fun ktFilesSequence(): Sequence<java.io.File> = changedFiles?.asSequence()
            ?: projectDir.walkTopDown().filter {
                it.isFile && it.extension == "kt" &&
                    !it.isIgnoredDetektPath()
            }

        val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(xmlReport)

        val issues = mutableMapOf<String, MutableMap<Int, MutableSet<String>>>()
        val fileNodes = doc.getElementsByTagName("file")

        for (i in 0 until fileNodes.length) {
            val fileNode = fileNodes.item(i) as Element
            val filePath = canonicalPath(fileNode.getAttribute("name"))
            if (changedPaths != null && filePath !in changedPaths) continue
            val errorNodes = fileNode.getElementsByTagName("error")

            for (j in 0 until errorNodes.length) {
                val error = errorNodes.item(j) as Element
                val lineNo = error.getAttribute("line").toIntOrNull() ?: continue
                val rule = error.getAttribute("source").substringAfterLast(".")

                val msg = error.getAttribute("message").replace("\n", " ").trim()
                val label = if (msg.isNotEmpty()) "$rule: $msg" else rule

                issues.getOrPut(filePath) { mutableMapOf() }
                    .getOrPut(lineNo) { mutableSetOf() }
                    .add(label)
            }
        }

        var modifiedCount = 0
        var totalAnnotations = 0
        val annotatedFiles = mutableListOf<String>()

        issues.forEach { (filePath, lineMap) ->
            val file = java.io.File(filePath)
            if (!file.exists()) return@forEach

            val lines = file.readLines(Charsets.UTF_8).toMutableList()
            var changed = false

            lineMap.toSortedMap().forEach { (lineNo, labels) ->
                val idx = lineNo - 1
                if (idx < 0 || idx >= lines.size) return@forEach
                val sortedLabels = labels.sorted()
                lines[idx] = stripDetektTodoFromLine(lines[idx], detektTodoRegex) +
                    " $marker [${sortedLabels.joinToString(", ")}]"
                changed = true
                totalAnnotations += sortedLabels.size
            }

            if (changed) {
                file.writeText(lines.joinToString("\n") + "\n")
                modifiedCount++
                annotatedFiles += filePath
            }
        }

        if (enableCustomRules) {
            // Enforce at most one consecutive blank line between code lines.
            ktFilesSequence().forEach { file ->
                val lines = file.readLines(Charsets.UTF_8).toMutableList()
                var changed = false
                var blankStreak = 0
                var hasSeenCodeLine = false
                val hasCodeAfterIndex = BooleanArray(lines.size)
                var seenCodeFromEnd = false

                for (i in lines.lastIndex downTo 0) {
                    hasCodeAfterIndex[i] = seenCodeFromEnd
                    if (lines[i].isNotBlank()) seenCodeFromEnd = true
                }

                for (i in lines.indices) {
                    val line = lines[i]
                    if (line.isBlank()) {
                        blankStreak++
                        if (
                            blankStreak > 1 &&
                            hasSeenCodeLine &&
                            hasCodeAfterIndex[i] &&
                            !line.contains("TooManyBlankLinesBetweenCodeLines")
                        ) {
                            lines[i] = stripDetektTodoFromLine(line, detektTodoRegex) +
                                " $marker [TooManyBlankLinesBetweenCodeLines: Maximum 1 consecutive blank line is allowed between code lines]"
                            changed = true
                            totalAnnotations++
                            if (file.absolutePath !in annotatedFiles) annotatedFiles += file.absolutePath
                        }
                    } else {
                        blankStreak = 0
                        hasSeenCodeLine = true
                    }
                }

                if (changed) {
                    file.writeText(lines.joinToString("\n") + "\n")
                    if (file.absolutePath !in annotatedFiles) modifiedCount++
                }
            }

            // Check @Preview functions for private visibility.
            val previewAnnotations = setOf("Preview")

            ktFilesSequence().forEach { file ->
                val lines = file.readLines(Charsets.UTF_8).toMutableList()
                var changed = false

                for (i in lines.indices) {
                    val trimmed = lines[i].trimStart()
                    val isPreview = previewAnnotations.any { ann ->
                        trimmed == "@$ann" || trimmed.startsWith("@$ann(") || trimmed.startsWith("@$ann ")
                    }
                    if (!isPreview) continue

                    for (j in (i + 1)..minOf(i + 8, lines.lastIndex)) {
                        val next = lines[j].trimStart()
                        if (next.startsWith("@")) continue
                        val hasFun = Regex("""^(suspend\s+)?(inline\s+)?(fun\s)""").containsMatchIn(next)
                        val isPrivate = next.startsWith("private ")
                        if (hasFun && !isPrivate && !lines[j].contains("PreviewMustBePrivate")) {
                            lines[j] = stripDetektTodoFromLine(lines[j], detektTodoRegex) +
                                " $marker [PreviewMustBePrivate: @Preview functions must be private]"
                            changed = true
                            totalAnnotations++
                            if (file.absolutePath !in annotatedFiles) annotatedFiles += file.absolutePath
                        }
                        break
                    }
                }

                if (changed) {
                    file.writeText(lines.joinToString("\n") + "\n")
                    if (file.absolutePath !in annotatedFiles) modifiedCount++
                }
            }

            // Check class primary constructors for inline parameters.
            val classPattern = Regex(
                """^\s*(?:(?:public|private|internal|protected|data|sealed|abstract|open|inner|value)\s+)*class\s""",
            )

            ktFilesSequence().forEach { file ->
                val lines = file.readLines(Charsets.UTF_8).toMutableList()
                var changed = false

                for (i in lines.indices) {
                    val line = lines[i]
                    if (!classPattern.containsMatchIn(line)) continue

                    val openIdx = line.indexOf('(')
                    if (openIdx < 0) continue

                    var depth = 0
                    var closeIdx = -1
                    for (k in openIdx until line.length) {
                        when (line[k]) {
                            '(' -> depth++
                            ')' -> {
                                depth--
                                if (depth == 0) { closeIdx = k; break }
                            }
                        }
                    }

                    if (closeIdx < 0) continue

                    val params = line.substring(openIdx + 1, closeIdx).trim()
                    if (params.isEmpty()) continue

                    if (countTopLevelParams(params) < 2) continue

                    if (!lines[i].contains("MultilineParametersRequired")) {
                        lines[i] = stripDetektTodoFromLine(lines[i], detektTodoRegex) +
                            " $marker [MultilineParametersRequired: Primary constructor parameters must each be on a separate line]"
                        changed = true
                        totalAnnotations++
                        if (file.absolutePath !in annotatedFiles) annotatedFiles += file.absolutePath
                    }
                }

                if (changed) {
                    file.writeText(lines.joinToString("\n") + "\n")
                    if (file.absolutePath !in annotatedFiles) modifiedCount++
                }
            }

            // Detect expression body functions (fun foo() = ...).
            val expressionBodyPattern = Regex(
                """^\s*(?:(?:override|public|private|internal|protected|open|abstract|inline|suspend|operator|infix|tailrec)\s+)*fun\s+[^=\n{]+\)\s*(?::\s*\S+\s*)?=\s*\S""",
            )

            ktFilesSequence().forEach { file ->
                val lines = file.readLines(Charsets.UTF_8).toMutableList()
                var changed = false

                for (i in lines.indices) {
                    if (!expressionBodyPattern.containsMatchIn(lines[i])) continue
                    if (lines[i].contains("ExpressionBodyNotAllowed")) continue

                    if (Regex("""fun\s+component\d+\s*\(""").containsMatchIn(lines[i])) continue
                    if (Regex("""fun\s+(get|set)\s*\(""").containsMatchIn(lines[i])) continue
                    val expressionBody = lines[i]
                        .substringAfterLast("=")
                        .replace(Regex("""//.*"""), "")
                        .trim()
                    if (Regex("""^[A-Za-z_][A-Za-z0-9_]*$""").matches(expressionBody)) continue
                    if (expressionBody == "null") continue

                    lines[i] = stripDetektTodoFromLine(lines[i], detektTodoRegex) +
                        " $marker [ExpressionBodyNotAllowed: Use block body { return ... } instead of expression body = ...]"
                    changed = true
                    totalAnnotations++
                    if (file.absolutePath !in annotatedFiles) annotatedFiles += file.absolutePath
                }

                if (changed) {
                    file.writeText(lines.joinToString("\n") + "\n")
                    if (file.absolutePath !in annotatedFiles) modifiedCount++
                }
            }
        } else {
            logger.lifecycle("  Custom annotate rules disabled (detektAnnotateCustomRules=false).")
        }

        logger.lifecycle("  $totalAnnotations annotation(s) added across $modifiedCount file(s).")
    }
}

tasks.register("detektFull") {
    description = "Full Detekt pipeline: clears TODO annotations → formats → scans entire project → re-annotates violations."
    group = "verification"
    dependsOn("detektClearTodos", "detektFormat", "detekt", "detektAnnotate")

    doLast {
        logger.lifecycle("")
        logger.lifecycle("  ✓ detektFull complete — check source files for '// TODO: Detekt' markers.")
        logger.lifecycle("")
    }
}

// ── Helper functions ──────────────────────────────────────────────────────────

fun clearDetektTodoAnnotations(
    projectDir: java.io.File,
    annotationRegex: Regex,
): Int {
    val files = projectDir.walkTopDown()
        .filter {
            it.isFile && it.extension == "kt" &&
                !it.isIgnoredDetektPath()
        }
        .toList()
    return clearDetektTodoAnnotationsInFiles(files, annotationRegex)
}

fun clearDetektTodoAnnotationsInFiles(
    files: Iterable<java.io.File>,
    annotationRegex: Regex,
): Int {
    var cleanedCount = 0
    files.forEach { file ->
        val content = file.readText()
        val cleanedLines = content
            .lineSequence()
            .map { line -> stripDetektTodoFromLine(line, annotationRegex) }
            .toMutableList()

        while (cleanedLines.isNotEmpty() && cleanedLines.last().isBlank()) {
            cleanedLines.removeAt(cleanedLines.lastIndex)
        }

        val normalized = cleanedLines.joinToString("\n")
        val cleaned = if (normalized.isEmpty()) "" else "$normalized\n"
        if (cleaned != content) {
            file.writeText(cleaned)
            cleanedCount++
        }
    }
    return cleanedCount
}

fun stripDetektTodoFromLine(
    line: String,
    annotationRegex: Regex,
): String {
    var cleaned = line
    while (true) {
        val match = annotationRegex.find(cleaned) ?: break
        cleaned = cleaned.removeRange(match.range)
    }
    return cleaned.trimEnd()
}

fun countTopLevelParams(params: String): Int {
    if (params.isBlank()) return 0
    var depth = 0
    var count = 1
    for (c in params) {
        when (c) {
            '(', '<', '[', '{' -> depth++
            ')', '>', ']', '}' -> depth--
            ',' -> if (depth == 0) count++
        }
    }
    return count
}
