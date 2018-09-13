/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.bedrock;

import net.daporkchop.lib.minecraft.util.packet.PacketRegistry;
import soupply.bedrock.Packet;
import soupply.bedrock.protocol.play.*;

/**
 * @author DaPorkchop_
 */
public class BedrockRegistry extends PacketRegistry<Packet> {
    public static final BedrockRegistry INSTANCE = new BedrockRegistry();

    private BedrockRegistry() {
        super(Packet::getId);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerPackets(Registry registry) {
        registry.registerAll(
                AddBehaviorTree::new,
                AddEntity::new,
                AddHangingEntity::new,
                AddItemEntity::new,
                AddPainting::new,
                AddPlayer::new,
                AdventureSettings::new,
                Animate::new,
                AvailableCommands::new,
                BlockEntityData::new,
                BlockEvent::new,
                BlockPickRequest::new,
                BookEdit::new,
                BossEvent::new,
                Camera::new,
                ChangeDimension::new,
                ChunkRadiusUpdated::new,
                ClientboundMapItemData::new,
                ClientToServerHandshake::new,
                CommandBlockUpdate::new,
                CommandOutput::new,
                CommandRequest::new,
                ContainerClose::new,
                ContainerOpen::new,
                ContainerSetData::new,
                CraftingData::new,
                CraftingEvent::new,
                Disconnect::new,
                EntityEvent::new,
                EntityFall::new,
                EntityPickRequest::new,
                Event::new,
                Explode::new,
                FullChunkData::new,
                GameRulesChanged::new,
                GuiDataPickItem::new,
                HurtArmor::new,
                Interact::new,
                InventoryContent::new,
                InventorySlot::new,
                InventoryTransaction::new,
                ItemFrameDropItem::new,
                LabTable::new,
                LevelEvent::new,
                LevelSoundEvent::new,
                Login::new,
                MapInfoRequest::new,
                MobArmorEquipment::new,
                MobEffect::new,
                MobEquipment::new,
                ModalFormRequest::new,
                ModalFormResponse::new,
                MoveEntityAbsolute::new,
                MoveEntityDelta::new,
                MovePlayer::new,
                NetworkStackLatency::new,
                NpcRequest::new,
                PhotoTransfer::new,
                PlayerAction::new,
                PlayerHotbar::new,
                PlayerInput::new,
                PlayerList::new,
                PlayerSkin::new,
                PlaySound::new,
                PlayStatus::new,
                PurchaseReceipt::new,
                RemoveEntity::new,
                RemoveObject::new,
                RequestChunkRadius::new,
                ResourcePackChunkData::new,
                ResourcePackChunkRequest::new,
                ResourcePackClientResponse::new,
                ResourcePackDataInfo::new,
                ResourcePacksInfo::new,
                ResourcePacksStackPacket::new,
                Respawn::new,
                RiderJump::new,
                ServerSettingsRequest::new,
                ServerSettingsResponse::new,
                ServerToClientHandshake::new,
                SetCommandsEnabled::new,
                SetDefaultGameType::new,
                SetDifficulty::new,
                SetDisplayObjective::new,
                SetEntityData::new,
                SetEntityLink::new,
                SetEntityMotion::new,
                SetHealth::new,
                SetLastHurtBy::new,
                SetLocalPlayerAsInitialized::new,
                SetPlayerGameType::new,
                SetScore::new,
                SetScoreboardIdentity::new,
                SetSpawnPosition::new,
                SetTime::new,
                SetTitle::new,
                ShowCredits::new,
                ShowProfile::new,
                ShowStoreOffer::new,
                SimpleEvent::new,
                SpawnExperienceOrb::new,
                StartGame::new,
                StopSound::new,
                StructureBlockUpdate::new,
                SubClientLogin::new,
                TakeItemEntity::new,
                Text::new,
                Transfer::new,
                UpdateAttributes::new,
                UpdateBlock::new,
                UpdateBlockSynced::new,
                UpdateEquip::new,
                UpdateSoftEnum::new,
                UpdateTrade::new,
                WSConnect::new
        );
    }
}
