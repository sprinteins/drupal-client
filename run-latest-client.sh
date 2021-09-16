#!/bin/sh

set -o errexit
set -o nounset
#set -o xtrace

OS="$(uname)"
if [ "${OS}" = "Linux" ]; then
  	platform=linux
elif [ "${OS}" = "Darwin" ]; then
  	platform=macos
else
	platform=windows
fi

cleanup() {
    currentExitCode=$?
    rm -f "./drupal-client"
    exit ${currentExitCode}
}

trap cleanup INT TERM EXIT

wget --quiet \
    --output-document="./drupal-client" \
    "https://github.com/sprinteins/drupal-client/releases/latest/download/drupal-client-${platform}"

chmod +x "./drupal-client"

"./drupal-client" --version

"./drupal-client" "$@"
