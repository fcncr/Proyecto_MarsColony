package com.mycompany.mars_colony;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import com.mycompany.mars_colony.servicio.ServicioCampania;
import java.util.ArrayList;
import java.util.List;

public class Mars_Colony {

    private static int pruebas = 0;
    private static int correctas = 0;

    public static void main(String[] args) {

        ImagenesEstado imagenes =
                new ImagenesEstado(null, null, null);

        NucleoOxigeno nucleo =
                new NucleoOxigeno(
                        "NUCLEO",
                        "Nucleo",
                        200,
                        imagenes,
                        new Posicion(12, 12)
                );

        Tablero tablero = new Tablero(nucleo);

        Escuadron escuadron = new Escuadron();

        Mision mision1 =
                crearMision(1, nucleo);

        Partida partida =
                new Partida(
                        "TEST_CAMPANIA",
                        escuadron,
                        tablero,
                        mision1
                );

        ServicioCampania servicio =
                new ServicioCampania();

        // =============================================
        // ESTADO INICIAL
        // =============================================

        verificar(
                "Capacidad inicial = 20",
                escuadron.getCapacidadTotal() == 20
        );

        verificar(
                "No registra victoria si la mision no fue ganada",
                !servicio.registrarVictoria(partida)
        );

        // =============================================
        // PRIMERA VICTORIA MISION 1
        // =============================================

        mision1.setEstado(
                EstadoMision.VICTORIA
        );

        mision1.cerrarRegistros();

        verificar(
                "Registro del nucleo queda cerrado",
                nucleo.getRegistroCombate().isCerrado()
        );

        verificar(
                "Primera victoria de mision 1 es nueva",
                servicio.registrarVictoria(partida)
        );

        verificar(
                "Capacidad 20 -> 25",
                escuadron.getCapacidadTotal() == 25
        );

        verificar(
                "Mision 1 registrada",
                partida.fueMisionSuperada(1)
        );

        // =============================================
        // DUPLICADO
        // =============================================

        verificar(
                "Segunda victoria de mision 1 no es nueva",
                !servicio.registrarVictoria(partida)
        );

        verificar(
                "Capacidad sigue en 25",
                escuadron.getCapacidadTotal() == 25
        );

        // =============================================
        // REPETIR
        // =============================================

        verificar(
                "Puede repetir mision actual",
                servicio.repetirActual(partida)
        );

        verificar(
                "Mision actual sigue siendo 1",
                partida.getMisionActual() == 1
        );

        verificar(
                "Estado vuelve a PREPARACION",
                mision1.getEstado()
                        == EstadoMision.PREPARACION
        );

        verificar(
                "Repeticion crea registro nuevo abierto",
                !nucleo.getRegistroCombate().isCerrado()
        );

        // Volvemos a simular victoria.
        mision1.setEstado(
                EstadoMision.VICTORIA
        );

        verificar(
                "Victoria repetida sigue sin bonificacion",
                !servicio.registrarVictoria(partida)
        );

        verificar(
                "Capacidad permanece en 25",
                escuadron.getCapacidadTotal() == 25
        );

        // =============================================
        // AVANZAR A MISION 2
        // =============================================

        verificar(
                "Avanza de mision 1 a 2",
                servicio.avanzar(partida)
        );

        verificar(
                "Mision actual = 2",
                partida.getMisionActual() == 2
        );

        // =============================================
        // COMPLETAR MISIONES 2 A 10
        // =============================================

        boolean progresionCorrecta = true;

        for (int numero = 2;
                numero <= 10;
                numero++) {

            Mision nuevaMision =
                    crearMision(numero, nucleo);

            partida.setMision(nuevaMision);

            nuevaMision.setEstado(
                    EstadoMision.VICTORIA
            );

            boolean nuevaVictoria =
                    servicio.registrarVictoria(
                            partida
                    );

            if (!nuevaVictoria) {
                progresionCorrecta = false;
                break;
            }

            if (numero < 10) {

                if (!servicio.avanzar(partida)) {
                    progresionCorrecta = false;
                    break;
                }
            }
        }

        verificar(
                "Misiones 2 a 10 progresan correctamente",
                progresionCorrecta
        );

        verificar(
                "Hay exactamente 10 misiones superadas",
                partida.getCantidadMisionesSuperadas()
                        == 10
        );

        verificar(
                "Capacidad tras 10 victorias = 70",
                escuadron.getCapacidadTotal() == 70
        );

        verificar(
                "Campania ya puede finalizar",
                servicio.puedeFinalizar(partida)
        );

        // =============================================
        // CONTINUAR DESPUES DE LA 10
        // =============================================

        verificar(
                "Siguiente mision extra calculada = 11",
                servicio.generarNumeroMisionExtra(
                        partida
                ) == 11
        );

        verificar(
                "Puede continuar despues de mision 10",
                servicio.avanzar(partida)
        );

        verificar(
                "Mision actual = 11",
                partida.getMisionActual() == 11
        );

        // =============================================
        // GANAR MISION 11
        // =============================================

        Mision mision11 =
                crearMision(11, nucleo);

        partida.setMision(mision11);

        mision11.setEstado(
                EstadoMision.VICTORIA
        );

        verificar(
                "Mision 11 puede registrarse",
                servicio.registrarVictoria(partida)
        );

        verificar(
                "Capacidad tras mision 11 = 75",
                escuadron.getCapacidadTotal() == 75
        );

        verificar(
                "Puede avanzar de 11 a 12",
                servicio.avanzar(partida)
        );

        verificar(
                "Mision actual = 12",
                partida.getMisionActual() == 12
        );

        // =============================================
        // FINALIZAR
        // =============================================

        verificar(
                "Puede finalizar incluso tras continuar",
                servicio.puedeFinalizar(partida)
        );

        verificar(
                "Finalizacion de campaña exitosa",
                servicio.finalizarCampania(partida)
        );

        verificar(
                "Partida queda marcada como finalizada",
                partida.isCampaniaFinalizada()
        );

        verificar(
                "No puede avanzar tras finalizar",
                !servicio.avanzar(partida)
        );

        // =============================================
        // RESULTADO
        // =============================================

        System.out.println();
        System.out.println(
                "===================================="
        );

        System.out.println(
                "RESULTADO: "
                + correctas
                + " / "
                + pruebas
        );

        System.out.println(
                "===================================="
        );

        if (correctas == pruebas) {

            System.out.println(
                    "TODO CORRECTO"
            );

        } else {

            System.out.println(
                    "HAY PRUEBAS QUE REVISAR"
            );
        }
    }

    private static Mision crearMision(
            int numero,
            NucleoOxigeno nucleo) {

        List<Criatura> criaturas =
                new ArrayList<>();

        List<ComponenteCombate> participantes =
                new ArrayList<>();

        participantes.add(nucleo);

        return new Mision(
                numero,
                0,
                criaturas,
                participantes,
                true
        );
    }

    private static void verificar(
            String descripcion,
            boolean resultado) {

        pruebas++;

        if (resultado) {

            correctas++;

            System.out.println(
                    "[OK] " + descripcion
            );

        } else {

            System.out.println(
                    "[ERROR] " + descripcion
            );
        }
    }
}