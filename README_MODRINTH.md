<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/TroIMIvXcwk" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>  

# Devil Fruit Circulatuion Bot 
This [Mine Mine no Mi](https://modrinth.com/mod/mine-mine-no-mi) Addon uses [Discord Integration](https://modrinth.com/plugin/dcintegration) to show the current devil fruit circulation in discord channels.  

It can display:
- Available fruits
- Unavailable fruits
- The current status of a fruit
- The current player attached to the status of the fruit
- The fruit tier defined by Emojis you can freely set

It also allows you to sort the fruits either alphabetically or by tier!

## Dependencies

This mod depends on the latest 1.16.5 version of [Discord Integration](https://modrinth.com/plugin/dcintegration) therefore you must get and configure this addon as well!

## Configuration Options

```toml
["Unavailable Embed Design"]
	#Color for the generated Embed in Hexadecimal
	"Color Hex" = "0xFFD700"
	#Channel ID to where the Unavailable Fruits Embed Message will be sent to. Please make sure the bot has write access!
	#Range: 0 ~ 9223372036854775807
	"Channel ID" = 0
	#Sort Devil Fruits by Alphabet
	"Sort by Alphabet" = false
	#DO NOT TOUCH. WILL BE GENERATED
	#Range: 0 ~ 9223372036854775807
	"Unavailable Message ID" = 0
	#Sort Devil Fruits by their Tier
	"Sort By Tier" = true
	#The footer of the Embed
	"Embed Footer" = "Made by DerHammerclock | Last updated"
	#Show a date next to the footer when the embed has been updated
	"Show Last Updated" = true

[General]
	#Show the current Status of the Fruit as well if it has one
	"Show Fruit Status" = true
	#Show the Available Fruits Embed
	"Show Available Embed" = true
	#Show the Unavailable Fruits Embed
	"Show Unavailable Embed" = true
	#Show the player name if the fruit status is INVENTORY or IN_USE. Requires Show Fruit Status to be true
	"Show Player Name as Status" = true

["Available Embed Design"]
	#Color for the generated Embed in Hexadecimal
	"Color Hex" = "0xFFD700"
	#Channel ID to where the Available Fruits Embed Message will be sent to. Please make sure the bot has write access!
	#Range: 0 ~ 9223372036854775807
	"Channel ID" = 0
	#Sort Devil Fruits by Alphabet
	"Sort by Alphabet" = false
	#Sort Devil Fruits by their Tier
	"Sort By Tier" = true
	#The footer of the Embed
	"Embed Footer" = "Made by DerHammerclock | Last updated"
	#DO NOT TOUCH. WILL BE GENERATED
	#Range: 0 ~ 9223372036854775807
	"Available Message ID" = 0
	#Show a date next to the footer when the embed has been updated
	"Show Last Updated" = true

[Emojis]
	#Discord Emoji ID to represent a Gold Box.
	#Range: 0 ~ 9223372036854775807
	"Gold Box Emoji ID" = 0
	#Discord Emoji ID to represent a Wooden Box.
	#Range: 0 ~ 9223372036854775807
	"Wooden Box Emoji ID" = 0
	#Use emojis to show the rarity of a fruit
	"Use Emojis" = true
	#Discord Emoji ID to represent an Iron Box.
	#Range: 0 ~ 9223372036854775807
	"Iron Box Emoji ID" = 0
```
