# drupal-client

## Installation guide

### With Dockerfile
#### Preconditions:
**For Windows:** You have to have Docker approved in GSN and installed on your laptop;

**For Unix:** You have to have Docker installed on your laptop;

#### Steps:
1. Install Docker on your machine
2. Build the project using 
`docker build -t cli-tool:latest .`
3. Run the project using 
`docker run --rm cli-tool:latest`
4. Bingo!
### Linux

#### Using the client in automation environments like Jenkins

The easiest way to ensure you're always running the latest version is using this script

`/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/sprinteins/drupal-client/HEAD/run-latest-client.sh) --version"`

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
2. Go to *Key authentication* (if this option is not available to you let the admins know and we can add you)
3. And *Generate a new key* if necessary
4. Open an Explorer window
5. Navigate to your profile directory by entering `%USERPROFILE%` in the address bar
6. Create a new directory `.config\drupal-client`
7. Create a new file `token.txt` and paste the key into it
8. Press `Win+R` and execute `rundll32 sysdm.cpl,EditEnvironmentVariables`
9. Create the `DHL_API_DEVELOPER_PORTAL_TOKEN_FILE` variable and set it to `%USERPROFILE%\.config\drupal-client\token.txt`

## User guide

This user guide provides instructions on how to use the CLI tool for Drupal system of DHL API Developer Portal, which has two main commands: `update` and `export`. Both commands share a set of common flags, which are detailed below.

### Commands

1. `update`: This command is used for uploading existing documents to the Drupal system.
2. `export`: This command is used to retrieve node's containment from the Drupal system.

### Common Flags

- `--debug`: Enable debug mode.
- `--insecure` or `-k`: Allow insecure server connections when using SSL.
- `--proxy` or `-x`: Use the specified proxy.
- `--noproxy`: Provide a list of hosts which do not use a proxy.
- `--api-page-directory`: Set the local path to the API page documentation (default: `api-docs`).
- `--token-file`: Specify the path to the file containing the authentication token. Can also be set via the environment variable `DHL_API_DEVELOPER_PORTAL_TOKEN_FILE` *(required)*.
- `--custom-html`: Keep custom HTML elements within the documentation.

### Usage

To use the CLI tool, you must specify the token file using the token argument.


Here's an example of how you might use the `update` command with flags:

```sh
$ cli-tool update --debug --insecure --proxy http://proxy.example.com --noproxy example.com,example.org --api-page-directory my-api-docs --token-file /path/to/token-file.txt --custom-html
```

And here's an example of how to use the export command with flags:
```shell
$ cli-tool export --debug --insecure --proxy http://proxy.example.com --noproxy example.com,example.org --api-page-directory my-api-docs --token-file /path/to/token-file.txt --custom-html
```

## Development

### GraalVM resource and reflection configuration

! Warning: This can be a bit tedious to figure out what is necessary ;) !

GraalVM needs to know which resources are used and which
classes are reflected at run/build-time. So it can be that
the Java version of your code works just fine but the GitHub
Actions build fails, or worse the client throws an error.

This config lives in `src/main/resources/META-INF/native-image`.

In case the build fails and you want to switch to your local
environment.

Install graalvm with e.g. `brew info graalvm-ce-java17`

Set your JAVA_HOME accordingly and call `./mvnw verify` and
reproduce the errors.

Then call the Java code with the agent that can intercept
the reflection and resource usage and automatically fill
the settings. You can find the configuration for this in 
the exec-maven-plugin configuration in the pom.xml.

! Don't just add everything !

It will add a lot of "local" files from e.g. IntelliJ.
You'll have to trial and error your way to what is necessary.

This has worked fine so far

 * jni-config.json
   * remove everything
 * reflect-config.json
   * remove `java.*`
   * remove `picocli.*`

Then try the build locally again and keep your fingers crossed.
