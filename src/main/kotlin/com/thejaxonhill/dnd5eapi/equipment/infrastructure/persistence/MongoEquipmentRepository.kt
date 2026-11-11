package com.thejaxonhill.dnd5eapi.equipment.infrastructure.persistence

import com.thejaxonhill.dnd5eapi.shared.domain.model.Index
import com.thejaxonhill.dnd5eapi.equipment.domain.Equipment
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentExample
import com.thejaxonhill.dnd5eapi.equipment.domain.EquipmentRepository
import com.thejaxonhill.dnd5eapi.shared.domain.model.Page
import com.thejaxonhill.dnd5eapi.shared.domain.model.PagedModel
import com.thejaxonhill.dnd5eapi.shared.infrastructure.paging.toPageable
import com.thejaxonhill.dnd5eapi.shared.infrastructure.persistence.converter.toPagedModel
import org.springframework.data.domain.Example
import org.springframework.stereotype.Component

@Component
class MongoEquipmentRepository(private val repository: EquipmentMongoRepository) : EquipmentRepository {
    override fun loadByIndex(index: Index): Equipment? = repository.findByIndex(index.value)?.toDomain()

    override fun loadAll(page: Page, example: EquipmentExample?): PagedModel<Equipment> =
        repository.findAll(
            Example.of(example?.toEquipmentDocument() ?: EquipmentDocument()),
            page.toPageable()
        ).toPagedModel().map { it.toDomain() }

    override fun loadAll(page: Page, query: String?): PagedModel<Equipment> =
        repository.findByNameContainsIgnoreCase(query ?: "", page.toPageable())
            .toPagedModel().map { it.toDomain() }
}

fun EquipmentDocument.toDomain() = Equipment(
    armorCategory = armorCategory,
    armorClass = armorClass,
    capacity = capacity,
    categoryRange = categoryRange,
    contents = contents,
    cost = cost!!,
    damage = damage,
    desc = desc,
    equipmentCategory = equipmentCategory!!,
    gearCategory = gearCategory,
    index = Index.of(index!!),
    name = name!!,
    properties = properties,
    quantity = quantity,
    range = range,
    special = special,
    speed = speed,
    stealthDisadvantage = stealthDisadvantage,
    strMinimum = strMinimum,
    throwRange = throwRange,
    toolCategory = toolCategory,
    twoHandedDamage = twoHandedDamage,
    vehicleCategory = vehicleCategory,
    weaponCategory = weaponCategory,
    weaponRange = weaponRange,
    weight = weight,
    url = url!!,
)

fun EquipmentExample.toEquipmentDocument(): EquipmentDocument = EquipmentDocument(
    name = name
)
