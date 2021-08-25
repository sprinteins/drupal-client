# drupal-client

## Getting started

### Installing the client

First of all you need to install the client for your operating system.

#### Windows

To be able to call the client from everywhere some setup is needed:

1. Open an Explorer window
2. Navigate to your profile directory by entering `%USERPROFILE%` in the address bar
3. Create a new directory called `bin`
4. [Download the current release](https://github.com/sprinteins/drupal-client/releases/latest/download/drupal-client-windows.exe) and save it in the `bin` directory as `drupal-client.exe`
5. Press `Win+R` and execute `rundll32 sysdm.cpl,EditEnvironmentVariables`
6. Edit the `Path` variable and add `%USERPROFILE%\bin` to it
7. Open a new Command Line Prompt and try `drupal-client --version`

### API Key

1. [Navigate to your profile in the API Developer Portal](https://developer.dhl.com/user/)
2. Go to *Key authentication*
3. And *Generate a new key* if necessary
