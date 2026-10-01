package com.seb.harvesteffect.model.contract;

/**
 * Contract for entities tethered to the Mass Relay FTL transport corridor.
 */
public interface RelayLinked {

    /**
     * Checks if the entity maintains an operational relay tether.
     *
     * @return true if connected to the relay network
     */
    boolean isLinked();

    /**
     * Connects entity to the nearest Mass Relay corridor.
     */
    void establishRelayLink();

    /**
     * Severs connection to the relay corridor.
     */
    void severRelayLink();

    /**
     * FTL transfer bandwidth multiplier (e.g. 1.0 base, 1.5 enhanced).
     *
     * @return bandwidth ratio
     */
    double getRelayBandwidth();
}
