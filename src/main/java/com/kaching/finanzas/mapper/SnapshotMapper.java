package com.kaching.finanzas.mapper;

import com.kaching.finanzas.dto.snapshots.GastoSnapshotDTO;
import com.kaching.finanzas.dto.snapshots.IngresoSnapshotDTO;
import com.kaching.finanzas.model.Gasto;
import com.kaching.finanzas.model.Ingreso;

public class SnapshotMapper {

    private SnapshotMapper() {}

    public static GastoSnapshotDTO toSnapshot(Gasto gasto) {
        return toSnapshot(gasto, gasto.getEliminado());
    }

    // Permite forzar el flag "eliminado". Se usa en CREAR: el estado "antes" es
    // el mismo registro marcado como eliminado (todavia no existia).
    public static GastoSnapshotDTO toSnapshot(Gasto gasto, boolean eliminado) {
        return new GastoSnapshotDTO(
                gasto.getId(),
                gasto.getPresupuesto().getId(),
                gasto.getMonto(),
                gasto.getDescripcion(),
                gasto.getPersona().getId(),
                gasto.getFechaGasto(),
                eliminado
        );
    }

    public static IngresoSnapshotDTO toSnapshot(Ingreso ingreso) {
        return toSnapshot(ingreso, ingreso.getEliminado());
    }

    public static IngresoSnapshotDTO toSnapshot(Ingreso ingreso, boolean eliminado) {
        return new IngresoSnapshotDTO(
                ingreso.getId(),
                ingreso.getPresupuesto().getId(),
                ingreso.getMonto(),
                ingreso.getDescripcion(),
                ingreso.getPersona().getId(),
                ingreso.getFechaIngreso(),
                eliminado
        );
    }
}
