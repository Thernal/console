#!/bin/sh
# Prints the notes of Console <version>'s GitHub release: its section of CHANGELOG.md, then the commits since the
# release before it by kind, then the compare link. release.yml writes a release's body with it, release-notes.yml
# writes it again for a release already made.
#   .github/release-notes.sh 1.0.0           the body (needs the tags: git fetch --tags)
#   .github/release-notes.sh --check 1.0.0   only fails when CHANGELOG.md has no section for it
set -eu
cd "$(dirname "$0")/.."
repo=${GITHUB_REPOSITORY:-Thernal/console}

check=
if [ "${1:-}" = --check ]; then
    check=1
    shift
fi
version=${1:?usage: release-notes.sh [--check] <version>}

# The lines under "## <version> — <date>" up to the next "## ", blank lines at either end dropped and each paragraph
# or list item on one line: GitHub draws a release's line breaks as written.
section=$(awk -v v="$version" '
    /^## / { if (found) exit; split($0, w, " "); if (w[2] == v) { found = 1; next } }
    found { print }
' CHANGELOG.md | sed -e '/./,$!d' | awk '
    /^[[:space:]]*$/ { if (buf != "") print buf; buf = ""; print; next }
    /^(- |#|[0-9]+\. )/ { if (buf != "") print buf; buf = $0; next }
    { sub(/^[[:space:]]+/, ""); buf = (buf == "" ? $0 : buf " " $0) }
    END { if (buf != "") print buf }
' | awk '{ lines[NR] = $0 } /./ { last = NR } END { for (i = 1; i <= last; i++) print lines[i] }')
if [ -z "$section" ]; then
    echo "release-notes.sh: CHANGELOG.md has no section for $version (## $version — <date>)" >&2
    exit 1
fi
[ -n "$check" ] && exit 0

tag=release/v$version
git rev-parse -q --verify "refs/tags/$tag" >/dev/null || {
    echo "release-notes.sh: no tag $tag here" >&2
    exit 1
}
# The release before this one: for a release candidate the tag just before it (1.0.0-rc.2 → 1.0.0-rc.1), for a
# final release the final one before it (1.0.0 → 0.3.1), so its notes hold everything its candidates did.
# versionsort.suffix sorts 1.0.0-rc.1 before 1.0.0.
tags=$(git -c versionsort.suffix=- tag --list 'release/v*' --sort=v:refname)
case $version in
    *-*) ;;
    *) tags=$(echo "$tags" | grep -v -e '-') ;;
esac
prev=$(echo "$tags" | awk -v t="$tag" '$0 == t { print p; exit } { p = $0 }')
range=$tag
[ -n "$prev" ] && range=$prev..$tag

# One kind of commit as a list: "feat(logging-ui): share logs …" → "- **logging-ui:** share logs … 22be5a2".
# A release's own commit (chore(release): …) is left out.
commits() {
    git log --no-merges --format='%s%x09%h' "$range" | awk -F '\t' -v kind="$1" '
        $1 ~ /^chore\(release\)/ { next }
        {
            subject = $1
            k = "other"
            if (subject ~ /^feat[(:!]/) k = "feat"
            else if (subject ~ /^fix[(:!]/) k = "fix"
            if (k != kind) next
            if (k != "other" && match(subject, /^[a-z]+(\([^)]*\))?!?: /)) {
                head = substr(subject, 1, RLENGTH - 2)
                subject = substr(subject, RLENGTH + 1)
                if (match(head, /\(.*\)/)) subject = "**" substr(head, RSTART + 1, RLENGTH - 2) ":** " subject
            }
            print "- " subject " " $2
        }'
}

printf '%s\n\n### Commits\n' "$section"
for kind in feat fix other; do
    list=$(commits "$kind")
    [ -z "$list" ] && continue
    case $kind in
        feat) title=Features ;;
        fix) title=Fixes ;;
        *) title=Other ;;
    esac
    printf '\n#### %s\n\n%s\n' "$title" "$list"
done
if [ -n "$prev" ]; then
    printf '\n**Full changelog**: https://github.com/%s/compare/%s...%s\n' "$repo" "$prev" "$tag"
else
    printf '\n**Full changelog**: https://github.com/%s/commits/%s\n' "$repo" "$tag"
fi
