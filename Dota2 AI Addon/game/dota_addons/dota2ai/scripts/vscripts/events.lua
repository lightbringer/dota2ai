-- Here are the event trigger that are called by the game on certain occasions asynchronously to the game's ticks

--------------------------------------------------------------------------------
-- GameEvent:OnGameRulesStateChange
--
-- Handles the game state cycles. Not much to do here yet
--
--------------------------------------------------------------------------------
function Dota2AI:OnGameRulesStateChange()
  local nNewState = GameRules:State_Get()  
  if nNewState == DOTA_GAMERULES_STATE_INIT then
    self:Reset()
  elseif nNewState == DOTA_GAMERULES_STATE_HERO_SELECTION then
    print( "OnGameRulesStateChange: Hero Selection" )
	PlayerResource:SetCustomTeamAssignment( 0, DOTA_TEAM_FIRST  ) -- put PlayerID 0 on Radiant team (== team 2)	
	PlayerResource:GetPlayer(0):MakeRandomHeroSelection()	--FIXME
  elseif nNewState == DOTA_GAMERULES_STATE_PRE_GAME then
    print( "OnGameRulesStateChange: Pre Game Selection" )
    SendToServerConsole( "dota_dev forcegamestart" ) -- Skip the draft process
  elseif nNewState == DOTA_GAMERULES_STATE_GAME_IN_PROGRESS then
    print( "OnGameRulesStateChange: Game In Progress" )
	BotPick()
	
	
	
	--Throwing in more bots to complete the teams. Activate the built-in AI if you like. 
	--Via the parameters, you can set the hero, initial lane, skill level, and team for a bot. The game does not
	--have behaviours for all different heroes though
    
    --Tutorial:AddBot( sHeroSelection[2], "top", "easy", true );
    --Tutorial:AddBot( sHeroSelection[3], "top", "easy", true );
    --Tutorial:AddBot( sHeroSelection[4], "bot", "easy", true );
    --Tutorial:AddBot( sHeroSelection[5], "bot", "easy", true );
    
    
    --Tutorial:AddBot( sHeroSelection[6], "top", "easy", false );
    --Tutorial:AddBot( sHeroSelection[7], "top", "easy", false );
    Tutorial:AddBot( Dota2AI.sHeroSelection[8], "mid", "easy", false );
    --Tutorial:AddBot( sHeroSelection[9], "bot", "easy", false );
    --Tutorial:AddBot( sHeroSelection[10], "bot", "easy", false );    
  end    
end

--------------------------------------------------------------------------------
-- GameEvent:OnHeroPicked
--
-- Once a hero is picked, a "context think function" is set that make makes a web call every time it's called
--------------------------------------------------------------------------------

 function Dota2AI:OnHeroPicked ()
	-- limited to player one for now
	local heroEntity = PlayerResource:GetSelectedHeroEntity(0)
	heroEntity:SetContextThink( "Dota2AI:BotThink", function() return Dota2AI:BotThink(heroEntity) end, 0.33 )
	 
	Say(nil, "Bot (team = " .. heroEntity:GetTeam()..", user=0 picked " .. heroEntity:GetName(), false)    
		
 end


--FIXME 
 --function Dota2AI:Reset()
  --request = CreateHTTPRequestScriptVM( "POST", Dota2AI.baseURL .. "/reset") 
  --request:SetHTTPRequestRawPostBody('application/json', "")
 --end

--------------------------------------------------------------------------------
-- GameEvent:OnHeroLevelUp
--
-- When a hero levels up, this function makes a web call to check which ability should be levelled
--------------------------------------------------------------------------------
 function Dota2AI:OnHeroLevelUp(event)  
	  local userid = event.player
	  local heroindex = event.heroindex;
	  local class = event.hero   
	  local heroEntity = EntIndexToHScript(heroindex)
	  
	  -- only make a web call if it's the controlled hero. Dota2AI:OnHeroLevelUp is also called for other heroes
	  if userid == 0 then
		self:BotLevelUp(heroEntity)
	  end
 end
 
 -- Helper function for Dota2AI:OnHeroLevelUp
  function Dota2AI:BotLevelUp(heroEntity) 
	  request = CreateHTTPRequestScriptVM( "POST", Dota2AI.baseURL .. "/levelup")
	  request:SetHTTPRequestHeaderValue("Accept", "application/json")
	  request:SetHTTPRequestHeaderValue("Content-Length", "0")
	  request:Send( function( result ) 
		  if result["StatusCode"] == 200 then       
			self:ParseHeroLevelUp(heroEntity, result['Body']) 
		  else
			Dota2AI.Error = true 
			for k,v in pairs( result ) do
			  Warning( string.format( "%s : %s\n", k, v ) )
			end   
		  end
	  end )
  end
 
 --------------------------------------------------------------------------------
 -- GameEvent:BotThink
 --
 -- Main function for a bot. Each tick, it makes a web call to determine if any web action should be taken
 -- The return value is the time in s when this function should be called again, and the function will
 -- halt and wait for the HTTP call to return. Thinking too long will prompt a warning in the game
 -- and should be sanytioned in the future
 --------------------------------------------------------------------------------
 function Dota2AI:BotThink(heroEntity)  
	  if heroEntity:GetAbilityPoints() > 0 then
		self:BotLevelUp(heroEntity)
	  end
	  request = CreateHTTPRequestScriptVM( "POST", Dota2AI.baseURL .. "/update")
	  request:SetHTTPRequestHeaderValue("Accept", "application/json")
	  request:SetHTTPRequestHeaderValue("X-Jersey-Tracing-Threshold", "VERBOSE" )
	  request:SetHTTPRequestRawPostBody('application/json', self:JSONWorld(heroEntity))
	  request:Send( function( result ) 
		if result["StatusCode"] == 200 then       
		  self:ParseHeroCommand(heroEntity, result['Body']) 
		else
		  Dota2AI.Error = true 
		  for k,v in pairs( result ) do
			Warning( string.format( "%s : %s\n", k, v ) )
		  end   
		  Warning("Request was:")
		  Warning(self:JSONWorld(heroEntity))
		end
	  end )
	  if self._Error == true then
		return 0
	  else
		return 0.33
	  end 
 end
 
 --------------------------------------------------------------------------------
 -- GameEvent:OnPlayerChat
 --
 -- This function doesn't do anything except forwarding chat messages of other players to the bot.
 -- I used it to control my test implementation, i.e. "bot go", "bot retreat", "bot attack" as simple chat commands
 --------------------------------------------------------------------------------
 function Dota2AI:OnPlayerChat(event)
  request = CreateHTTPRequestScriptVM( "POST", Dota2AI.baseURL .. "/chat")
  request:SetHTTPRequestHeaderValue("Accept", "application/json")
  request:SetHTTPRequestHeaderValue("X-Jersey-Tracing-Threshold", "VERBOSE" )
  request:SetHTTPRequestRawPostBody('application/json', self:JSONChat(event))
  request:Send( function( result ) 
    if result["StatusCode"] == 200 then       
      --TODO what to do actually?
    else
      Dota2AI.Error = true 
      for k,v in pairs( result ) do
        Warning( string.format( "%s : %s\n", k, v ) )
      end   
      Warning("Request was:")
      Warning(self:JSONChat(event))
    end
  end )
 end

 function Dota2AI:OnBaseURLChanged(event)
	Dota2AI.baseURL = CustomNetTables:GetTableValue( "game_state", "base_url" )["value"]
	print("New base URL " .. Dota2AI.baseURL)
 end
 
 function BotPick()
	local baseURL = CustomNetTables:GetTableValue( "game_state", "base_url" )["value"]
	request = CreateHTTPRequestScriptVM( "POST", baseURL .. "/select")
	request:SetHTTPRequestHeaderValue("Accept", "application/json")
	request:SetHTTPRequestHeaderValue("X-Jersey-Tracing-Threshold", "VERBOSE" )
	request:SetHTTPRequestHeaderValue("Content-Length", "0")
	request:Send( function( result ) 
    
	if result["StatusCode"] == 200 then       
      local command = package.loaded['game/dkjson'].decode(result['Body'])    
	  print("Bot returned: \"" .. command.hero .. "\"")
	  
	  PrecacheUnitByNameAsync(command.hero, 
		function( )
			--CreateHeroForPlayer( command.hero, PlayerResource:GetPlayer(0)	) 
			PlayerResource:ReplaceHeroWith( 0, command.hero, 0, 0) --FIXME
			Dota2AI:OnHeroPicked()
		end,
		0)	  		
    else
      Dota2AI.Error = true 
      for k,v in pairs( result ) do
        Warning( string.format( "%s : %s\n", k, v ) )
      end   
    end
  end )
 end

