-- This file holds functions that handle the responses from a bot and calls the appropriate LUA functions
--

Dota2AI._Error = false;

local function BitAND(a,b)--Bitwise and
    local p,c=1,0
    while a>0 and b>0 do
        local ra,rb=a%2,b%2
        if ra+rb>1 then c=c+p end
        a,b,p=(a-ra)/2,(b-rb)/2,p*2
    end
    return c
end

 function Dota2AI:ParseHeroLevelUp(eHero, reply)	
	local result = package.loaded['game/dkjson'].decode(reply)
	local abilityPoints = eHero:GetAbilityPoints()
	if abilityPoints <= 0 then
		Warning(eHero:GetName() .. " has no ability points. Why am I levelling up?")
		return
	end
	
	local abilityIndex = result.abilityIndex
	
	if abilityIndex > -1 then --a bot may send -1 to delay the level up
		local ability = eHero:GetAbilityByIndex(abilityIndex)
		if ability:GetLevel() == ability:GetMaxLevel() then
			Warning(eHero:GetName() .. ": " .. ability:GetName() .. " is maxed out")
			return
		end		
		ability:UpgradeAbility(false)
		eHero:SetAbilityPoints(abilityPoints - 1) --UpgradeAbility doesn't decrease the ability points
		Say(nil, eHero:GetName() .. " levelled up ability " .. abilityIndex, false)
	end
 end

 -- Main entry function --
 function Dota2AI:ParseHeroCommand(eHero, reply)	
	local result = package.loaded['game/dkjson'].decode(reply)
	local command = result.command
	
	--TODO deal with abilities that the hero can interrupt
	local eAbility = eHero:GetCurrentActiveAbility()
	if eAbility then 
		self:Noop(eHero, result)
		Warning("active not null")
		return
	end
	
	if command == "MOVE"  then
		self:MoveTo(eHero, result)
	elseif command == "ATTACK" then
		self:Attack(eHero, result)
	elseif command == "CAST" then
		self:Cast(eHero, result)
	elseif command == "BUY" then
		self:Buy(eHero, result)
	elseif command == "SELL" then
		self:Sell(eHero, result)
	elseif command == "USE_ITEM" then
		self:UseItem(eHero, result)
	elseif command == "NOOP" then
		self:Noop(eHero, result)
	else 
		self._Error = true
		Warning(eHero:GetName() .. " sent invalid command " .. reply)
	end
 end

-- Buying items --
function Dota2AI:Buy(eHero, result)
	local itemName = result.item
	local itemCost = GetItemCost(itemName)
	
	if eHero:GetGold() < itemCost then
		Warning(eHero:GetName() .. " tried to buy " .. itemName .. " but couldn't afford it")
		return
	end
	

	local targetSlot = -1
	for i=DOTA_ITEM_SLOT_1 ,DOTA_ITEM_SLOT_6 ,1 do 
		if not eHero:GetItemInSlot(i) then
			targetSlot = i
			break
		end
	end
	
	--TODO item availability is missing
	
	if targetSlot < 0 then
		Warning(eHero:GetName() .. " tried to buy " .. itemName .. " but has no space left")		
		--TODO buy into stash
		return
	end
	
	local eShop = Entities:FindByClassnameWithin(nil, "trigger_shop", eHero:GetOrigin(), 0.01)		
	if eShop then 
		local shopType = GetShopType(eShop:GetModelName())
		
		if IsAvailableInShop(itemName, shopType) then
			
			local eItem = CreateItem(itemName, eHero, eHero)		
			EmitSoundOn("General.Buy", eHero)
			eHero:AddItem(eItem)			
			eHero:SpendGold(itemCost, DOTA_ModifyGold_PurchaseItem) --should the reason take DOTA_ModifyGold_PurchaseConsumable into account?  
		else
			--TODO ping actually the right shop
			PingNearestShop(eHero)
			Warning("Shop is of type " .. shopType)		
			return
		end
	else
		PingNearestShop(eHero)
		return
	end
	
	Say(nil, eHero:GetName() .." bought " .. itemName, false)
end

function Dota2AI:Sell(eHero, result)
	local slot = result.slot
	
	
	if eHero:CanSellItems() then 
		local eItem = eHero:GetItemInSlot(slot)
		if eItem then
			--TODO GetCost does not return the value altered, i.e. halved
			EmitSoundOn("General.Sell", eHero)
			eHero:ModifyGold(eItem:GetCost(), true, DOTA_ModifyGold_SellItem ) 
			eHero:RemoveItem(eItem)
		else
			Warning("No item in slot " .. slot)
		end
	else
		PingNearestShop(eHero)
		Warning("Bot tried to sell item outside shop")
	end
end

function Dota2AI:UseItem(eHero, result)
	local slot = result.slot
	local eItem = eHero:GetItemInSlot(slot)
	if eItem then
		self:UseAbility(eHero, eItem)
	else
		Warning("Bot tried to use item in empty slot")
	end
end
 
 function Dota2AI:Noop(eHero, result)
	--Noop
end
 
function Dota2AI:MoveTo(eHero, result)
	eHero:MoveToPosition(Vector(result.x, result.y, result.z))
	Say(nil, eHero:GetName() .. " moving to " .. result.x ..", " .. result.y ..", " .. result.z, false)
end

function Dota2AI:Attack(eHero, result)
	--Might want to check attack range
	--eHero:PerformAttack(EntIndexToHScript(result.target), true, true, false, true) 
	eHero:MoveToTargetToAttack(EntIndexToHScript(result.target))
	Say(nil, eHero:GetName() .. " attacking " .. result.target, false)
end

function Dota2AI:Cast(eHero, result)
	local eAbility = eHero:GetAbilityByIndex(result.ability)
	self:UseAbility(eHero, eAbility)		
end

function Dota2AI:UseAbility(eHero, eAbility)
	local level = eAbility:GetLevel()
	local manaCost = eAbility:GetManaCost(level)
	local player = eHero:GetPlayerOwnerID()
	
	if eHero:GetMana() < manaCost then 
		Warning("Bot tried to use ability without mana")
	elseif eAbility:GetCooldownTimeRemaining() > 0 then
		Warning("Bot tried to use ability still on cooldown")
	else
		eAbility:StartCooldown(eAbility:GetCooldown(level))
		eAbility:PayManaCost()
		eAbility:OnSpellStart()
		local behaviour = eAbility:GetBehavior()
		--There is some logic missing here to check for range and make the hero face the right direction	
		if (BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_NO_TARGET)) then
			Say(nil ,eHero:GetName() .. " casting " .. eAbility:GetName(), false)
			eHero:CastAbilityNoTarget(eAbility, player)
		elseif(BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_UNIT_TARGET )) then
			local target = EntIndexToHScript(result.target)
			if target:IsAlive() then
				Say(nil ,eHero:GetName() .. " casting " .. eAbility:GetName() .. " on unit " .. target:GetName(), false)		
				eHero:CastAbilityOnTarget(target, eAbility, player)
			end
		elseif(BitAND(behaviour, DOTA_ABILITY_BEHAVIOR_POINT )) then
			Say(nil ,eHero:GetName() .. " casting " .. eAbility:GetName() .. " on " .. result.x .. ", " .. result.y .. ", " .. result.z, false)
			eHero:CastAbilityOnPosition(Vector(result.x, result.y, result.z), eAbility, player)
		else
			Warning(eHero:GetName() .. " sent invalid cast command " .. behaviour)
			self._Error = true
		end
		
		
	end
end