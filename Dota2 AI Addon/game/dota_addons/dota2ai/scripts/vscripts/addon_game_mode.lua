 function BotPick()
	request = CreateHTTPRequest( "POST", Dota2AI.baseURL .. "/select")
	request:SetHTTPRequestHeaderValue("Accept", "application/json")
	request:SetHTTPRequestHeaderValue("X-Jersey-Tracing-Threshold", "VERBOSE" )
	request:Send( function( result ) 
    
	if result["StatusCode"] == 200 then       
      local command = package.loaded['game/dkjson'].decode(result['Body'])    
	  print("Bot returned: \"" .. command.hero .. "\"")
		GameRules:GetGameModeEntity():SetCustomGameForceHero( command.hero	) 
	  --Dota2AI.selectedHero  = command.hero	
    else
      Dota2AI.Error = true 
      for k,v in pairs( result ) do
        Warning( string.format( "%s : %s\n", k, v ) )
      end   
    end
  end )
 end



if Dota2AI == nil then
	_G.Dota2AI = class({})
end




------------------------------------------------------------------------------------------------------------------------------------------------------
-- Required .lua files, which just exist to help organize functions contained in our addon.  Make sure to call these beneath the mode's class creation.
------------------------------------------------------------------------------------------------------------------------------------------------------
require( "json_helpers" )
require( "hero_tools" )
require( "utility_functions" )
require( "events" )

--------------------------------------------------------------------------------
-- ACTIVATE
--------------------------------------------------------------------------------
function Activate()
    GameRules.Dota2AI = Dota2AI()
    GameRules.Dota2AI:InitGameMode()
	
end


require ("config")
Dota2AI.firstRun = true



--------------------------------------------------------------------------------
-- INIT
--------------------------------------------------------------------------------
function Dota2AI:InitGameMode()
	

	print( "Initializing Dota2 AI mode" )
	local GameMode = GameRules:GetGameModeEntity()
	
	GameMode:SetTowerBackdoorProtectionEnabled( true )
	--GameMode:SetFixedRespawnTime( 4 )
	GameMode:SetBotThinkingEnabled( true ) -- the ConVar is currently disabled in C++
	--GameMode:SetFogOfWarDisabled(true)
	-- Set bot mode difficulty: can try 
	--GameMode:SetCustomGameDifficulty( 1 )

	--GameRules:SetUseUniversalShopMode( false )
	--GameRules:SetPreGameTime( 1 )
	GameRules:SetCustomGameSetupTimeout( 0 ) -- skip the custom team UI with 0, or do indefinite duration with -1
	
	
	GameMode:SetContextThink( "HeroDemo:GameThink", function() return self:GameThink() end, 0 )

	-- Events
	ListenToGameEvent( "game_rules_state_change", Dynamic_Wrap( Dota2AI, 'OnGameRulesStateChange' ), self )
	ListenToGameEvent("dota_player_pick_hero", Dynamic_Wrap(Dota2AI, 'OnHeroPicked'), self)
	ListenToGameEvent("player_chat", Dynamic_Wrap(Dota2AI, 'OnPlayerChat'), self)
	
	--ListenToGameEvent( "npc_spawned", Dynamic_Wrap( Dota2AI, "OnNPCSpawned" ), self )
	--ListenToGameEvent( "dota_item_purchased", Dynamic_Wrap( CHeroDemo, "OnItemPurchased" ), self )
	--ListenToGameEvent( "npc_replaced", Dynamic_Wrap( CHeroDemo, "OnNPCReplaced" ), self )

	--CustomGameEventManager:RegisterListener( "WelcomePanelDismissed", function(...) return self:OnWelcomePanelDismissed( ... ) end )
	--CustomGameEventManager:RegisterListener( "RefreshButtonPressed", function(...) return self:OnRefreshButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "LevelUpButtonPressed", function(...) return self:OnLevelUpButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "MaxLevelButtonPressed", function(...) return self:OnMaxLevelButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "FreeSpellsButtonPressed", function(...) return self:OnFreeSpellsButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "InvulnerabilityButtonPressed", function(...) return self:OnInvulnerabilityButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "SpawnAllyButtonPressed", function(...) return self:OnSpawnAllyButtonPressed( ... ) end ) -- deprecated
	--CustomGameEventManager:RegisterListener( "SpawnEnemyButtonPressed", function(...) return self:OnSpawnEnemyButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "LevelUpEnemyButtonPressed", function(...) return self:OnLevelUpEnemyButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "DummyTargetsButtonPressed", function(...) return self:OnDummyTargetsButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "RemoveSpawnedUnitsButtonPressed", function(...) return self:OnRemoveSpawnedUnitsButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "LaneCreepsButtonPressed", function(...) return self:OnLaneCreepsButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "ChangeHeroButtonPressed", function(...) return self:OnChangeHeroButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "ChangeCosmeticsButtonPressed", function(...) return self:OnChangeCosmeticsButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "PauseButtonPressed", function(...) return self:OnPauseButtonPressed( ... ) end )
	--CustomGameEventManager:RegisterListener( "LeaveButtonPressed", function(...) return self:OnLeaveButtonPressed( ... ) end )


	

 end



--------------------------------------------------------------------------------
-- Main Think
--------------------------------------------------------------------------------
function Dota2AI:GameThink()
	if Dota2AI.firstRun == true then
		BotPick()
		PlayerResource:SetCustomTeamAssignment( 0, DOTA_TEAM_FIRST  ) -- put PlayerID 0 on Radiant team (== team 2)
		Tutorial:StartTutorialMode()
		Dota2AI.firstRun = false
	end
	return 0.5
end

print( "Dota2 AI  game mode loaded." )