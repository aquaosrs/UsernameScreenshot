package com.UsernameScreenshot;

import lombok.Data;

// Shamelessly stolen from https://github.com/cepawiel/RuneLite-Discord-Notifications
@Data
class DiscordWebhookBody
{
    private String content;
    private Embed embed;

    @Data
    static class Embed
    {
        final UrlEmbed image;
    }

    @Data
    static class UrlEmbed
    {
        final String url;
    }
}