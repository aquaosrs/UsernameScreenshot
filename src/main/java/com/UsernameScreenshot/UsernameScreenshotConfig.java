package com.UsernameScreenshot;

import net.runelite.client.config.*;

@ConfigGroup("usernamescreenshot")
public interface UsernameScreenshotConfig extends Config
{
    @ConfigItem(
            keyName = "uploadToImgur",
            name = "Upload to Imgur",
            description = "Uploads the screenshot to Imgur, and copies the url to your clipboard",
            position = 0
    )
    default boolean uploadToImgur() { return true; }

    @ConfigItem(
            keyName = "uploadToDiscord",
            name = "Upload to Discord",
            description = "Uploads the screenshot to Discord, and copies the url to your clipboard",
            position = 1
    )
    default boolean uploadToDiscord() { return true; }

    @ConfigSection(
            name = "Discord Webhook Settings",
            description = "The config for webhook content notifications",
            position = 2,
            closedByDefault = false
    )
    String webhookConfig = "webhookConfig";

    @ConfigItem(
            keyName = "webhook",
            name = "Webhook URL(s)",
            description = "The Discord Webhook URL(s) to send messages to, separated by a newline.",
            section = webhookConfig,
            position = 0
    )
    String webhook();

    @ConfigItem(
            keyName = "discordMessage",
            name = "Discord Message",
            description = "The message to send to Discord alongside the screenshot",
            section = webhookConfig,
            position = 1
    )
    default String discordMessage() { return "$name found this account in the wild: $target"; }
}
