## About
Server-side Fabric mod adding commands that give the player permanent attribute modifiers at the cost of a large amount of a single item.

By default contains 43 purchasable buffs designed for a completionist playthrough.

## Commands
- `/avarus-get <item>`\
  example: `/avarus-get carrot_512` - gives 1 max hp for 512 carrots\
  Provides a buff by consuming the required number of items (unique to each item) from the player's inventory.
- `/avarus-help`\
  Shows a short help message explaining the mod and its functionality.
- `/avarus-status`\
  Tells you how many buffs are active, which are missing.
- `/avarus-get-all`\
  Provides all the buffs for free at once. Requires permission level 2 and creative mode.
- `/avarus-remove <item>`\
  Removes a buff. Requires permission level 2.
- `/avarus-remove-all`\
  Removes all buffs. Requires permission level 2.

## Configuration

The configuration can be found at `config/avarus.json`.

Buff entry structure:

- string name:  Optional. The name of the buff used in commands.
  - `{itemId}_{itemsRequired}` is used if not specified.
- string itemId: Identifier of the item used to obtain the buff.
- int    itemsRequired: Number of items required to obtain the buff.
- string attributeId: Identifier of the Attribute the player gains from the buff
- double value: Value of the gained attribute as a floating point number.
- string operationId: Operation type. Valid settings: `addition`, `multiply_base`, `multiply_total`.

[Default configuration](https://github.com/wdfeer/avarus/blob/1.21.1/src/main/resources/data/avarus/default_buffs.json):

```
{
  "buffs": [
    {
      "itemId": "minecraft:sugar",
      "itemsRequired": 512,
      "attributeId": "minecraft:generic.movement_speed",
      "value": 0.075,
      "operationId": "multiply_base"
    },
    {
      "itemId": "minecraft:feather",
      "itemsRequired": 512,
      "attributeId": "minecraft:generic.movement_speed",
      "value": 0.075,
      "operationId": "multiply_base"
    },
    ...
  ] 
}
```
