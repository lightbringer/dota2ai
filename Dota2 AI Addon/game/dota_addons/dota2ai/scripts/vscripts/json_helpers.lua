-- This file contains functions that serialise game entities into JSON 



function Dota2AI:JSONChat(event)	
	jsonEvent = {}
	jsonEvent.teamOnly = event.teamonly -- we should probably test one day if the bot is on the same team
    jsonEvent.player = event.userid
    jsonEvent.text = event.text

	return package.loaded['game/dkjson'].encode(jsonEvent)
end

 function Dota2AI:JSONtree(eTree)	
	local tree = {}
	tree.origin = VectorToArray(eTree:GetOrigin())
	tree.type = "Tree"
	return tree
 end
 
 function Dota2AI:JSONunit(eUnit)	
	local unit = {}
	unit.level = eUnit:GetLevel()
	unit.origin = VectorToArray(eUnit:GetOrigin())
	--unit.absOrigin = VectorToArray(eUnit:GetAbsOrigin())
	--unit.center = VectorToArray(eUnit:GetCenter())
	--unit.velocity = VectorToArray(eUnit:GetVelocity())
	--unit.localVelocity = VectorToArray(eUnit:GetForwardVector())
	unit.health = eUnit:GetHealth()
	unit.maxHealth = eUnit:GetMaxHealth()
	unit.mana = eUnit:GetMana()
	unit.maxMana = eUnit:GetMaxMana()
	unit.alive = eUnit:IsAlive()
	unit.blind = eUnit:IsBlind()	
	unit.dominated = eUnit:IsDominated()
	unit.deniable = eUnit:IsDeniable()
	unit.disarmed = eUnit:IsDisarmed()
	unit.rooted = eUnit:IsRooted()
	unit.name = eUnit:GetName()
	unit.team = eUnit:GetTeamNumber()
	unit.attackRange = eUnit:GetAttackRange()	
	
	if eUnit:IsHero() then
		unit.gold = eUnit:GetGold()
		unit.type = "Hero"
		-- Abilities are actually in CBaseNPC, but we'll just send them for Heros to avoid cluttering the JSON--
		unit.abilities = {}
		local abilityCount = eUnit:GetAbilityCount() - 1 --minus 1 because lua for loops are upper boundary inclusive
		
		for index=0,abilityCount,1 do			
			local eAbility = eUnit:GetAbilityByIndex(index)
			-- abilityCount returned 16 for me even though the hero had only 5 slots (maybe it's actually max slots?). We fix that by checking for null pointer
			if eAbility then
				unit.abilities[index] = {}
				unit.abilities[index].type = "Ability"
				unit.abilities[index].name = eAbility:GetAbilityName()
				unit.abilities[index].targetFlags = eAbility:GetAbilityTargetFlags()
				unit.abilities[index].targetTeam = eAbility:GetAbilityTargetTeam()
				unit.abilities[index].targetType = eAbility:GetAbilityTargetType()
				unit.abilities[index].abilityType = eAbility:GetAbilityType()
				unit.abilities[index].abilityIndex = eAbility:GetAbilityIndex()
				unit.abilities[index].level = eAbility:GetLevel()				
				unit.abilities[index].maxLevel = eAbility:GetMaxLevel()
				unit.abilities[index].abilityDamage = eAbility:GetAbilityDamage()
				unit.abilities[index].abilityDamageType = eAbility:GetAbilityDamage()
				unit.abilities[index].cooldownTime = eAbility:GetCooldownTime()
				unit.abilities[index].cooldownTimeRemaining = eAbility:GetCooldownTimeRemaining()
			end
		end
	elseif eUnit:IsBuilding() then
		if eUnit:IsTower() then
			unit.type = "Tower"
		else
			unit.type = "Building"
		end
	else
		unit.type = "BaseNPC"
	end
	
	
	local attackTarget = eUnit:GetAttackTarget()
	if attackTarget then
		unit.attackTarget = attackTarget:entindex()
	end
	
	return unit
 end
 
-- At the moment, we serialise the whole game state visible to a team
-- a future TODO would be only sending those entities that have changed 
function Dota2AI:JSONWorld(eHero)	
	local world = {}
	world.entities = {}
	

	--TODO there are apparently around 2300 trees on the map. Sending those that are NOT standing might be more efficient
	local tree = Entities:FindByClassname(nil, "ent_dota_tree")
	while tree ~= nil do
		if eHero:CanEntityBeSeenByMyTeam(tree) and tree:IsStanding() then
			world.entities[tree:entindex()]=self:JSONtree(tree)
		end		
		tree = Entities:FindByClassname(tree, "ent_dota_tree")
	end

	
	local allUnits = FindUnitsInRadius(eHero:GetTeamNumber(), 
                              eHero:GetOrigin(),
                              nil,
                              FIND_UNITS_EVERYWHERE,
                              DOTA_UNIT_TARGET_TEAM_BOTH,
                              DOTA_UNIT_TARGET_ALL,
                              DOTA_UNIT_TARGET_FLAG_FOW_VISIBLE,
                              FIND_ANY_ORDER,
                              true)
							 
	for _,unit in pairs(allUnits) do
		world.entities[unit:entindex()]=self:JSONunit(unit)
	end
	
	--so FindUnitsInRadius somehow ignores all the buildings
	local buildings = {}
	buildings[0] =  Entities:FindByName(nil, "dota_goodguys_tower1_bot")
	buildings[1] =  Entities:FindByName(nil, "dota_goodguys_tower2_bot")
	buildings[2] =  Entities:FindByName(nil, "dota_goodguys_tower3_bot")
	
	buildings[3] =  Entities:FindByName(nil, "dota_goodguys_tower1_mid")
	buildings[4] =  Entities:FindByName(nil, "dota_goodguys_tower2_mid")
	buildings[5] =  Entities:FindByName(nil, "dota_goodguys_tower3_mid")
	
	buildings[6] =  Entities:FindByName(nil, "dota_goodguys_tower1_top")
	buildings[7] =  Entities:FindByName(nil, "dota_goodguys_tower2_top")
	buildings[8] =  Entities:FindByName(nil, "dota_goodguys_tower3_top")
	
	buildings[9] =  Entities:FindByName(nil, "dota_goodguys_tower4_top")
	buildings[10] =  Entities:FindByName(nil, "dota_goodguys_tower4_bot")
	
	buildings[11] =  Entities:FindByName(nil, "good_rax_melee_bot")
	buildings[12] =  Entities:FindByName(nil, "good_rax_range_bot")
	buildings[13] =  Entities:FindByName(nil, "good_rax_melee_mid")
	buildings[14] =  Entities:FindByName(nil, "good_rax_range_mid")
	buildings[15] =  Entities:FindByName(nil, "good_rax_melee_top")
	buildings[16] =  Entities:FindByName(nil, "good_rax_range_top")
	
	buildings[17] =  Entities:FindByName(nil, "ent_dota_fountain_good")
	
	--dire
	buildings[18] =  Entities:FindByName(nil, "dota_badguys_tower1_bot")
	buildings[19] =  Entities:FindByName(nil, "dota_badguys_tower2_bot")
	buildings[20] =  Entities:FindByName(nil, "dota_badguys_tower3_bot")
	
	buildings[21] =  Entities:FindByName(nil, "dota_badguys_tower1_mid")
	buildings[22] =  Entities:FindByName(nil, "dota_badguys_tower2_mid")
	buildings[23] =  Entities:FindByName(nil, "dota_badguys_tower3_mid")
	
	buildings[24] =  Entities:FindByName(nil, "dota_badguys_tower1_top")
	buildings[25] =  Entities:FindByName(nil, "dota_badguys_tower2_top")
	buildings[26] =  Entities:FindByName(nil, "dota_badguys_tower3_top")
	
	buildings[27] =  Entities:FindByName(nil, "dota_badguys_tower4_top")
	buildings[28] =  Entities:FindByName(nil, "dota_badguys_tower4_bot")
	
	buildings[29] =  Entities:FindByName(nil, "bad_rax_melee_bot")
	buildings[30] =  Entities:FindByName(nil, "bad_rax_range_bot")
	buildings[31] =  Entities:FindByName(nil, "bad_rax_melee_mid")
	buildings[32] =  Entities:FindByName(nil, "bad_rax_range_mid")
	buildings[33] =  Entities:FindByName(nil, "bad_rax_melee_top")
	buildings[34] =  Entities:FindByName(nil, "bad_rax_range_top")
	
	buildings[35] =  Entities:FindByName(nil, "ent_dota_fountain_bad")
	
	
	for i,unit in ipairs(buildings) do
		world.entities[unit:entindex()]=self:JSONunit(unit)
	end
	
	return package.loaded['game/dkjson'].encode(world)
end 
 
 function VectorToArray(v)
	return {v.x, v.y, v.z}
 end
 
 