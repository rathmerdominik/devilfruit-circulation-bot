<p align="center">
 <img src="src/main/resources/dfcirc.png" height="500" width="500"/>

<h1 align="center">Devil Fruit Circulation Bot</h1>

<p align="center"> Devil Fruit Circulation Bot is a Minecraft Addon for the popular <a href="https://www.curseforge.com/minecraft/mc-mods/mine-mine-no-mi"> Mine Mine no Mi Mod</a> mod.</p>

<p align="center">
 <img src="image.png"/>

## Installation

```
git clone https://github.com/rathmerdominik/MineMineNoMiDevilFruitCirculationBot.git
cd MineMineNoMiDevilFruitCirculationBot
curl -L https://www.curseforge.com/api/v1/mods/78726/files/4682386/download -o mine-mine-no-mi-1.16.5-0.9.5.jar
mv mine-mine-no-mi-1.16.5-0.9.5.jar libs
./gradlew build
```

From there you can take the jar out of the `build/libs` folder

## Configuration Options

```
[General]
	#Show the current Status of the Fruit as well if it has one
	#Available Status: LOST, IN_USE, INVENTORY, DROPPED
	#Default: False
	"Show Fruit Status" = false
	#In which mode the Bot is supposed to work.
	#Allowed Values: ONLY_SHOW_AVAILABLE, SHOW_AVAILABLE_AND_UNAVAILABLE, ONLY_SHOW_UNAVAILABLE
	"Circulation Bot Mode" = "ONLY_SHOW_AVAILABLE"
	#Channel ID to where the Circulation Messages will be send. Please make sure the bot has write access!
	#Range: 0 ~ 9223372036854775807
	"Channel ID" = 1000000000000000000
	#Server ID in which the Devil Fruit Circulation Bot should work in
	#Range: 0 ~ 9223372036854775807
	"Server ID" = 100000000000000000
	#Your discord bot token here.
	"Discord Bot Token" = ""
	#DO NOT TOUCH. WILL BE GENERATED
	#Range: 0 ~ 9223372036854775807
	"Message ID" = 0

	[General.EmbedDesign]
		#Color for the generated Embed in Hexadecimal
		"Color Hex" = "0xFFD700"
		#Sort Devil Fruits by Alphabet
		"Sort by Alphabet" = false
		#Sort Devil Fruits by their Tier
		"Sort By Tier" = true
		#The title of the Embed
		"Embed Title" = "Current Devilfruit Circulation"
		#The footer of the Embed
		"Embed Footer" = "Made by DerHammerclock | Last updated"
		#Show a date next to the footer when the embed has been updated
		"Show Last Updated" = true

	[General.Emojis]
		#Discord Emoji ID to represent a Gold Box.
		#Range: 0 ~ 9223372036854775807
		"Gold Box Emoji ID" = 1000000000000000000
		#Discord Emoji ID to represent a Wooden Box.
		#Range: 0 ~ 9223372036854775807
		"Wooden Box Emoji ID" = 1000000000000000000
		#Use emojis to show the rarity of a fruit
		"Use Emojis" = false
		#Discord Emoji ID to represent an Iron Box.
		#Range: 0 ~ 9223372036854775807
		"Iron Box Emoji ID" = 1000000000000000000
```