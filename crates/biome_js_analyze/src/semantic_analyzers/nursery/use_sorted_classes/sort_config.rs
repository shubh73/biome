//! The following structures define the config required to compute sort-related information about
//! CSS classes (`ClassInfo`) that is later used to compare and sort them. A sort config includes:
//! - The list of layers, in order.
//! - The list of utilities, in order, for each layer.
//! - The list of variants, in order of importance (which is used to compute the variants weight).
//! - Other options, such as prefix and separator.

use std::collections::HashMap;

/// A utility layer, containing its name and an ordered list of classes.
pub struct UtilityLayer {
    pub name: String,
    pub classes: &'static [&'static str],
}

/// The utilities config, contains an ordered list of utility layers.
pub type UtilitiesConfig = Vec<UtilityLayer>;

/// The variants config, contains an ordered list of variants.
pub type VariantsConfig = Vec<String>;

/// The sort config, containing the utility config and the variant config.
pub struct SortConfig {
    pub utilities: UtilitiesConfig,
    pub variants: VariantsConfig,
    pub layer_index_map: HashMap<String, usize>,
}

impl SortConfig {
    /// Creates a new sort config.
    pub fn new(utilities_config: UtilitiesConfig, variants: VariantsConfig) -> Self {
        // Compute the layer index map.
        let mut layer_index_map: HashMap<String, usize> = HashMap::new();
        let mut index = 0;
        for layer in utilities_config.iter() {
            layer_index_map.insert(layer.name.clone(), index);
            index += 1;
        }
        layer_index_map.insert("arbitrary".to_string(), index);

        Self {
            utilities: utilities_config,
            variants,
            layer_index_map,
        }
    }
}
