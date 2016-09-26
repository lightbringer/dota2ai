# The Dota2 AI Framework project
**General disclaimer: this project is not affiliated with Valve. It's an academic research project.** 

##What is this purpose of this project?
Writing bots for video games is a discipline with growing popularity both in academia and among gaming/coding enthusiast. You may have heard about the "Starcraft Bot competition" or the "2K Bot Prize" for Unreal Tournament. The motivations are manifold: some are just unsatisfied with the built-in AI, others see this as a research challenge and go for questions like: "can we build machines no human can beat?" or "what does it take to fool a human that she is playing against another human?"

This project aims to make it easier to do this for Dota2. Though Dota2 shares a similar popularity like games with Starcraft, my personal hunch is that it's not as complex (for an AI) to play sufficiently well. Instead, I would like to lay the focus on coordination as on bot controls only one hero and has to work together with human team mates or other bots.

##Goals and aims
The goal is to provide a framework for bots to fully compete in a standard match. Both writing the best Dota2 AI ever made or bots that pass a turing-like test are viable directions. As a start, I'm aiming for bots that can reliably farm the middle lane and eventually destroy the enenmy T1 middle tower in a 1v! setting; like the game's online practice mode.

##So why this framework? What does it do?
The game offers a LUA sandbox for scripting and modding various aspect of the game, meant for people who would like to create new content such as game modes, heroes, abilities and whatnot. While it would eventually possible to write a more complex bot in LUA, most people would prefer something else to code in. That means they could re-use existing libraries and code that has been tested in other domains. Also, coding a bot i LUA would mean that a bot can't do any machine learning task, e.g. very time consuming functions, asynchronously, since everything runs in the same process and would slow things down a lot. Also, coding bots in the LUA sandbox would require additional measures to prevent them from cheating.

The Dota2 mode is a copy of the basic game mode and the default map, but acts as a proxy between the game and a web service through JSON objects. Though this complicates things a bit, it is unfortunately the only way, as the game only allows HTTP requests to access other processes, as the io package and loadlib are not supported by built-in LUA runtime. The repository also provides an example implementation of a bot, but rather for demonstration purposes.

##Isn't that dangerous (malware, viruses, etc)?
The mod itself can't harm your computer, as the Dota2 LUA Sandbox is very restrictive about what it can or can't do. It uses a HTTP connection to talk to bot authors' software. However, as these are mostly executable programs, standard caution should be applied - like with everthing that you download from the internet.

##What's the state of the mod? Does it come with cool bots to play against with?
No. This is a research project and should encourage people to contribute their own AIs. Not all things a human can do in the game are currently implemented. I started with some basic things like moving, attack, casting speels, and buying and selling items. However, I decided to put it up on github so others can contribute and this project hopefully gains some traction.

##I heard about bots before, it's about cheating, right?
No. This is a copy of the base game (a custom game). You can't use this to improve your MMR online.

##How do I get started?
- At the moment, you need to have the Workshoptools for Dota2 installed. 
- You should also be familiar with Dota2 modding. I will try to make this easier in the future if you just want to write a mod and not contribute this project
- Copy the files from Dota2 AI Addon into the Dota2 game folder.
- Edit *game/dota_addons/dota2ai/scripts/vscripts/config.lua* to your likings
- Start the workshop tools. Load the *dota2ai* project
- In the asset browser, click the button to launch the vConsole
- the the console window type `dota_launch_custom_game dota2ai dota`
 
This is how you start the mod. However, unless you configured config.lua to point to a working bot web service, nothing will happen.

**Note:** I also uploaded the current version to the [Steam Workshop](http://steamcommunity.com/sharedfiles/filedetails/?id=770366571)

##Assuming the mod works, how do I write bots?
The mod assumes that there are a number of POST urls that accept and return JSON relative to the base URL that you configure in config.lua. I posted a brief description of them along with the transmitted messages [here](https://github.com/lightbringer/dota2ai/wiki/API). I tried to be brief and to give you "the big picture". For more detail, have a look at my LUA and Java code if you want to start writing.

##What exactly is this reference implementation?
It's a Maven project that I created with eclipse to debug basic commands. It runs on NanoHTTP. I split it up into two parts to make it more accesible for people who are neither familiar nor interested in web programming. The first part is the *Dota2BotFramework*. It's a web application that provides a web service and a basic abstraction model for a bot to use. For it to function, it needs a bot in its classpath. I made a quick tutorial on how to use this [here](https://github.com/lightbringer/dota2ai/wiki/The-Example-Bot). You basically drop a jar in its classpath that implements the `Bot`interface and specifies a service in the META-INF directory.

The second project, The *Example Bot* is a simple state machine for a Lina, that can go to the middle lane, farm, and randomly cast spells. She also retreats if her health falls under a certain percentage. You can control her with same basic commands in team chat like "lina go" or "lina buy tango". Not very complex, but everybody has to start somewhere :)

##How can I contribute?
I put the code on github so everybody can fork it and improve it. Familiarising yourself with the LUA code and the DOta2 Scripting AI is probably a good starting point. From an AI perspective, writing state machines is probably the easiest way to start. However, this is where my scope ends and you should read up on general game AI literature.

If you'd like to reach out, pitch me a mail.

Tobias Mahlmann, PhD (Lund University Sweden) tobias.mahlmann@lucs.lu.se
