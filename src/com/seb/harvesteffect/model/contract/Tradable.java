package com.seb.harvesteffect.model.contract;

/**
 * Contract for resources and components exchangeable at the Citadel Nexus.
 */
public interface Tradable {

    /**
     * Valuation of the asset measured in refined Element Zero (Eezo).
     *
     * @return market value in Eezo
     */
    int getEezoValue();

    /**
     * Designation of the tradable asset.
     *
     * @return readable item name
     */
    String getItemName();

    /**
     * Detailed tactical description.
     *
     * @return description
     */
    String getDescription();
}
