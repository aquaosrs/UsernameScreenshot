package com.UsernameScreenshot;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.ImageUploadStyle;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Provider;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.http.api.RuneLiteAPI.GSON;


@PluginDescriptor(
		name = "Username Screenshot",
		description = "For when you want to share OSRS usernames you see in the wild.",
		tags = {"screenshot", "username", "players", "discord"}
)
@Slf4j
public class UsernameScreenshotPlugin extends Plugin {
	private static final String SCREENSHOT = "Screenshot";

	@Inject
	private Client client;

	@Inject
	private Provider<MenuManager> menuManager;
	@Inject
	private ImageCapture imageCapture;

	@Inject
	private UsernameScreenshotConfig config;

	@Provides
	UsernameScreenshotConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(UsernameScreenshotConfig.class);
	}

	@Inject
	private DrawManager drawManager;
	private BufferedImage menuImage;

	@Inject
	private OkHttpClient okHttpClient;

	@Override
	protected void startUp() throws Exception
	{
		if (client != null)
		{
			menuManager.get().addPlayerMenuItem(SCREENSHOT);
		}
	}


	@Override
	protected void shutDown() throws Exception
	{
		if (client != null)
		{
			menuManager.get().removePlayerMenuItem(SCREENSHOT);
		}
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		if(Arrays.stream(event.getMenuEntries()).anyMatch(entry -> entry.getOption().equals(SCREENSHOT))) {
			log.debug("Screenshot option found");
			drawManager.requestNextFrameListener(image ->
			{
				menuImage = (BufferedImage) image;
			});
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuAction() == MenuAction.RUNELITE_PLAYER && event.getMenuOption().equals(SCREENSHOT))
		{
			Player player = event.getMenuEntry().getPlayer();
			if (player == null)
			{
				return;
			}

			double scaleFactor = client.getStretchedDimensions().getWidth() / client.getRealDimensions().getWidth();

			String target = player.getName();

			BufferedImage screenshot = new BufferedImage(getMenuWidth(scaleFactor), getMenuHeight(scaleFactor), BufferedImage.TYPE_INT_ARGB);

			Graphics graphics = screenshot.getGraphics();

			if(menuImage != null) {
				graphics.drawImage(menuImage.getSubimage(getMenuX(scaleFactor), getMenuY(scaleFactor), getMenuWidth(scaleFactor), getMenuHeight(scaleFactor)), 0, 0, null);

				ImageUploadStyle style = config.uploadToImgur() ? ImageUploadStyle.IMGUR : ImageUploadStyle.NEITHER;

				imageCapture.takeScreenshot(screenshot, target + ".png", "UsernameScreenshots", false, style);

				if(config.uploadToDiscord()) {
					String localName = client.getLocalPlayer().getName();

					String discordString = config.discordMessage().replaceAll("\\$name", localName).replaceAll("\\$target", target);

					sendWebhook(new DiscordWebhookBody(){
						{
							setContent(discordString);
						}
					}, screenshot);
				}
			}
		}
	}

	private int getMenuWidth(double scaleFactor) {
		return (int) (client.getMenuWidth() * scaleFactor);
	}

	private int getMenuHeight(double scaleFactor) {
		return (int) (client.getMenuHeight() * scaleFactor);
	}

	private int getMenuX(double scaleFactor) {
		return (int) (client.getMenuX() * scaleFactor);
	}

	private int getMenuY(double scaleFactor) {
		return (int) (client.getMenuY() * scaleFactor);
	}

	// Here down is shamelessly stolen from https://github.com/cepawiel/RuneLite-Discord-Notifications
	private void sendWebhook(DiscordWebhookBody discordWebhookBody, BufferedImage image) {
		String configUrl = config.webhook();
		if (Strings.isNullOrEmpty(configUrl)) { return; }

		List<String> webhookUrls = Arrays.asList(configUrl.split("\n"))
				.stream()
				.filter(u -> u.length() > 0)
				.map(u -> u.trim())
				.collect(Collectors.toList());

		for (String webhookUrl : webhookUrls)
		{
			HttpUrl url = HttpUrl.parse(webhookUrl);
			MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("payload_json", GSON.toJson(discordWebhookBody, DiscordWebhookBody.class));

			byte[] screenshotBytes;
			try
			{
				screenshotBytes = convertImageToByteArray(image);
			}
			catch (IOException e)
			{
				log.warn("Error converting image to byte array", e);
				return;
			}

			requestBodyBuilder.addFormDataPart("file", "image.png",
					RequestBody.create(MediaType.parse("image/png"), screenshotBytes));
			buildRequestAndSend(url, requestBodyBuilder);
		}
	}

	private static byte[] convertImageToByteArray(BufferedImage bufferedImage) throws IOException
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	private void buildRequestAndSend(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
	{
		RequestBody requestBody = requestBodyBuilder.build();
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		sendRequest(request);
	}

	private void sendRequest(Request request)
	{
		okHttpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.debug("Error submitting webhook", e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				response.close();
			}
		});
	}
}