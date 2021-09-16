# drupal-client

## Getting started

### Linux

#### Using the client in automation environments like Jenkins

The easiest way to ensure you're always running the latest version is using this script

`/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/sprinteins/drupal-client/HEAD/run-latest-client.sh)" --version`

#### API Key in an Environment variable

**! We do absolutely do not recommend passing secrets via environment variables !**

See [https://smallstep.com/blog/command-line-secrets/](https://smallstep.com/blog/command-line-secrets/) for details on why it's a bad idea.

If you have *absolutely no* other choice you can pass the token to the client like this

`drupal-client update --token-file=<(echo "${DHL_API_DEVELOPER_PORTAL_TOKEN}")`

or set the environment variable like this

`DHL_API_DEVELOPER_PORTAL_TOKEN_FILE=<(echo "${DHL_API_DEVELOPER_PORTAL_TOKEN}") sh ./some-script-that-calls-the-client.sh`

Be aware that it only works for the one call so *exporting the variable does not work*.

### Windows

#### Installing/Updating the client with PowerShell

`Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/sprinteins/drupal-client/HEAD/install-latest-client.ps1'))`

#### Installing the client manually

To be able to call the client from everywhere some setup is needed:

1. Open an Explorer window
2. Navigate to your profile directory by entering `%USERPROFILE%` in the address bar
3. Create a new directory `bin`
4. [Download the current release](https://github.com/sprinteins/drupal-client/releases/latest/download/drupal-client-windows.exe) and save it in the `bin` directory as `drupal-client.exe`
5. Press `Win+R` and execute `rundll32 sysdm.cpl,EditEnvironmentVariables`
6. Edit the `Path` variable and add `%USERPROFILE%\bin` to it
7. Open a new Command Line Prompt and try `drupal-client --version`

#### Setting up a global API Key

With a global API key you can skip the `--token-file` parameter for the client invocations.
There is no support for multiple environments at the moment.

1. [Navigate to your profile in the API Developer Portal](https://developer.dhl.com/user/)
2. Go to *Key authentication*
3. And *Generate a new key* if necessary
4. Open an Explorer window
5. Navigate to your profile directory by entering `%USERPROFILE%` in the address bar
6. Create a new directory `.config\drupal-client`
7. Create a new file `token.txt` and paste the key into it
8. Press `Win+R` and execute `rundll32 sysdm.cpl,EditEnvironmentVariables`
9. Create the `DHL_API_DEVELOPER_PORTAL_TOKEN_FILE` variable and set it to `%USERPROFILE%\.config\drupal-client\token.txt`
