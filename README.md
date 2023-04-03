# Username Screenshot

This plugin adds an option to the right click player menu to take a screenshot of the player's username, optionally sends to discord channels.

![](https://i.imgur.com/siTH8cP.png)

## Config

![](https://i.imgur.com/AtMTQWM.png)

### Upload to Imgur

If enabled, the screenshot will be uploaded to Imgur and the link will be copied to your clipboard.

### Upload to Discord

If enabled, the screenshot will be sent to each of the channels specified in the Discord Webhook Settings - Webhook URL(s) field.

#### Webhook URL(s)
Comma separated list of Discord webhook URLs to send the screenshot to.

#### Discord Message
The message to sent alongside the screenshot in Discord.

$name will be replaced with the logged in player's username.
$target will be replaced with the username of the screenshot user.

## Viewing the screenshots

### File Service
*Cannot be turned off*

When screenshot is taken it will take a screenshot of the player's username and save it to your screenshots folder for the currently logged in player and the sub folder ```/UserNameScreenshots```.

![](https://i.imgur.com/jdvftqo.png)

### Imgur 
*Requires Upload to Imgur to be enabled*

When screenshot is taken it will take a screenshot of the player's username and upload it to Imgur. The imgur link will be copied to your clipboard.

### Discord
*Requires Upload to Discord to be enabled*

When screenshot is taken it will take a screenshot of the player's username and send it to the Discord channels specified in the Discord Webhook Settings - Webhook URL(s) field.

With the message from the Discord Message field.

![](https://i.imgur.com/NdsuRBF.png)

#### Setting up Discord Webhook
This is a plugin for RuneLite which sends a message to your discord channel when you level up, complete a quest or when you die. The message includes what skill you levelled or quest you completed and optionally a screenshot as proof. 

In order to use this plugin you will need to create a webhook on your discord server. Below are instructions for how to do so:
1. Open your Server Settings and head into the Integrations tab
2. Click the "Create Webhook" button to create a new webhook
3. Once created, copy the webhook URL and insert into the "Webhook URL" box in the plugin's configuration in RuneLite. 

![image](https://user-images.githubusercontent.com/13265450/109745517-9b42ef80-7b99-11eb-82f9-63f8ea590e8c.png)


## Help and discussion

If you've experienced an issue with this plugin, or have a recommendation on how to improve it, please [create an issue](https://github.com/aquaosrs/UsernameScreenshot/issues/new) with the relevant details.
