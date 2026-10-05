package com.mycompany.mars_colony;

import com.mycompany.mars_colony.motor.MotorBatalla;
import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.combate.criatura.Acechador;
import com.mycompany.mars_colony.modelo.combate.criatura.Demoledor;
import com.mycompany.mars_colony.modelo.combate.criatura.Enjambre;
import com.mycompany.mars_colony.modelo.combate.criatura.Volador;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaContacto;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaImpacto;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaMultiple;
import com.mycompany.mars_colony.modelo.combate.defensa.Dron;
import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;
import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;
import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;
import java.util.ArrayList;
import java.util.List;

public class Mars_Colony {

    private static int pruebas = 0;
    private static int correctas = 0;

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("   PRUEBA FINAL - UNIDADES ESPECIALES");
        System.out.println("======================================");

        probarDron();
        probarDefensaImpacto();
        probarDefensaMultiple();
        probarDemoledor();
        probarVolador();
        probarEnjambre();

        System.out.println();
        System.out.println("======================================");
        System.out.println("             RESULTADO FINAL");
        System.out.println("======================================");
        System.out.println("Pruebas correctas: " + correctas + " / " + pruebas);

        if (correctas == pruebas) {
            System.out.println("TODO CORRECTO");
        } else {
            System.out.println("HAY PRUEBAS QUE REVISAR");
        }
    }

    // =========================================================
    // 20.1 DRON
    // =========================================================

    private static void probarDron() {

        System.out.println();
        System.out.println("----- 20.1 DRON -----");

        ImagenesEstado imagenes = new ImagenesEstado(null, null, null);

        EstadisticasCombate statsDron = new EstadisticasCombate(
                30, 10, 1, 1, 0, 3,
                false, 1, 1, 300
        );

        EstadisticasCombate statsAcechador = new EstadisticasCombate(
                30, 5, 1, 1, 0, 0,
                false, 1, 1, 500
        );

        NucleoOxigeno nucleo = crearNucleo(imagenes);
        Tablero tablero = new Tablero(nucleo);

        Dron dron = new Dron(
                "DRON-01",
                "Dron",
                statsDron,
                imagenes,
                1,
                new Posicion(1, 1)
        );

        Acechador acechador = new Acechador(
                "ACE-01",
                "Acechador",
                statsAcechador,
                imagenes,
                1,
                new Posicion(6, 1)
        );

        tablero.colocar(dron, new Posicion(1, 1));
        tablero.colocar(acechador, new Posicion(6, 1));

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(acechador);

        List<ComponenteCombate> participantes = new ArrayList<>();
        participantes.add(nucleo);
        participantes.add(dron);
        participantes.add(acechador);

        MotorBatalla motor = crearMotor(tablero, criaturas, participantes);

        motor.iniciar();

        for (int i = 0; i < 20 && motor.estaEnEjecucion(); i++) {
            dron.ejecutarCiclo(motor, 100);
        }

        verificar(
                "Dron se desplaza hasta quedar en alcance",
                dron.getPosicion().equals(new Posicion(5, 1))
        );

        verificar(
                "Dron destruye al Acechador",
                acechador.estaDestruido()
        );

        verificar(
                "La mision termina en VICTORIA",
                motor.getPartida().getMision().getEstado() == EstadoMision.VICTORIA
        );
    }

    // =========================================================
    // 20.2 DEFENSA IMPACTO
    // =========================================================

    private static void probarDefensaImpacto() {

        System.out.println();
        System.out.println("----- 20.2 DEFENSA IMPACTO -----");

        ImagenesEstado imagenes = new ImagenesEstado(null, null, null);

        EstadisticasCombate statsImpacto = new EstadisticasCombate(
                20, 20, 1, 1, 2, 5,
                false, 1, 10, 0
        );

        EstadisticasCombate statsCriatura = new EstadisticasCombate(
                10, 5, 1, 1, 0, 0,
                false, 1, 1, 500
        );

        NucleoOxigeno nucleo = crearNucleo(imagenes);
        Tablero tablero = new Tablero(nucleo);

        DefensaImpacto impacto = new DefensaImpacto(
                "IMP-01",
                "Impacto",
                statsImpacto,
                imagenes,
                1,
                new Posicion(10, 10)
        );

        Acechador criatura1 = new Acechador(
                "ACE-01",
                "Criatura 1",
                statsCriatura,
                imagenes,
                1,
                new Posicion(10, 11)
        );

        Acechador criatura2 = new Acechador(
                "ACE-02",
                "Criatura 2",
                statsCriatura,
                imagenes,
                1,
                new Posicion(11, 10)
        );

        tablero.colocar(impacto, new Posicion(10, 10));
        tablero.colocar(criatura1, new Posicion(10, 11));
        tablero.colocar(criatura2, new Posicion(11, 10));

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(criatura1);
        criaturas.add(criatura2);

        List<ComponenteCombate> participantes = new ArrayList<>();
        participantes.add(nucleo);
        participantes.add(impacto);
        participantes.add(criatura1);
        participantes.add(criatura2);

        MotorBatalla motor = crearMotor(tablero, criaturas, participantes);

        motor.iniciar();

        impacto.ejecutarCiclo(motor, 100);

        verificar(
                "Impacto destruye criatura 1 dentro del radio",
                criatura1.estaDestruido()
        );

        verificar(
                "Impacto destruye criatura 2 dentro del radio",
                criatura2.estaDestruido()
        );

        verificar(
                "DefensaImpacto se autodestruye",
                impacto.estaDestruido()
        );
    }

    // =========================================================
    // 20.3 DEFENSA MULTIPLE
    // =========================================================

    private static void probarDefensaMultiple() {

        System.out.println();
        System.out.println("----- 20.3 DEFENSA MULTIPLE -----");

        ImagenesEstado imagenes = new ImagenesEstado(null, null, null);

        EstadisticasCombate statsMultiple = new EstadisticasCombate(
                100, 5, 1, 3, 0, 8,
                false, 3, 3, 0
        );

        EstadisticasCombate statsCriatura = new EstadisticasCombate(
                30, 1, 1, 1, 0, 0,
                false, 1, 1, 500
        );

        NucleoOxigeno nucleo = crearNucleo(imagenes);
        Tablero tablero = new Tablero(nucleo);

        DefensaMultiple multiple = new DefensaMultiple(
                "MUL-01",
                "Defensa Multiple",
                statsMultiple,
                imagenes,
                1,
                new Posicion(10, 10)
        );

        Acechador a = new Acechador(
                "A-01", "A",
                statsCriatura, imagenes, 1,
                new Posicion(10, 11)
        );

        Acechador b = new Acechador(
                "B-01", "B",
                statsCriatura, imagenes, 1,
                new Posicion(11, 10)
        );

        Acechador c = new Acechador(
                "C-01", "C",
                statsCriatura, imagenes, 1,
                new Posicion(11, 11)
        );

        tablero.colocar(multiple, new Posicion(10, 10));
        tablero.colocar(a, new Posicion(10, 11));
        tablero.colocar(b, new Posicion(11, 10));
        tablero.colocar(c, new Posicion(11, 11));

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(a);
        criaturas.add(b);
        criaturas.add(c);

        List<ComponenteCombate> participantes = new ArrayList<>();
        participantes.add(nucleo);
        participantes.add(multiple);
        participantes.add(a);
        participantes.add(b);
        participantes.add(c);

        MotorBatalla motor = crearMotor(tablero, criaturas, participantes);

        motor.iniciar();

        multiple.ejecutarCiclo(motor, 100);

        verificar(
                "Ataque multiple golpea al objetivo A",
                a.getVidaActual() == 25
        );

        verificar(
                "Ataque multiple golpea al objetivo B",
                b.getVidaActual() == 25
        );

        verificar(
                "Ataque multiple golpea al objetivo C",
                c.getVidaActual() == 25
        );

        motor.detener();
    }

    // =========================================================
    // 20.4 DEMOLEDOR
    // =========================================================

    private static void probarDemoledor() {

        System.out.println();
        System.out.println("----- 20.4 DEMOLEDOR -----");

        ImagenesEstado imagenes = new ImagenesEstado(null, null, null);

        EstadisticasCombate statsDemoledor = new EstadisticasCombate(
                30, 15, 1, 1, 2, 0,
                false, 1, 10, 500
        );

        EstadisticasCombate statsDefensa = new EstadisticasCombate(
                50, 1, 1, 1, 0, 5,
                false, 1, 1, 0
        );

        NucleoOxigeno nucleo = crearNucleo(imagenes);
        Tablero tablero = new Tablero(nucleo);

        Demoledor demoledor = new Demoledor(
                "DEM-01",
                "Demoledor",
                statsDemoledor,
                imagenes,
                1,
                new Posicion(2, 5)
        );

        DefensaContacto defensa1 = new DefensaContacto(
                "DEF-01",
                "Defensa 1",
                statsDefensa,
                imagenes,
                1,
                new Posicion(5, 5)
        );

        DefensaContacto defensa2 = new DefensaContacto(
                "DEF-02",
                "Defensa 2",
                statsDefensa,
                imagenes,
                1,
                new Posicion(5, 6)
        );

        tablero.colocar(demoledor, new Posicion(2, 5));
        tablero.colocar(defensa1, new Posicion(5, 5));
        tablero.colocar(defensa2, new Posicion(5, 6));

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(demoledor);

        List<ComponenteCombate> participantes = new ArrayList<>();
        participantes.add(nucleo);
        participantes.add(defensa1);
        participantes.add(defensa2);
        participantes.add(demoledor);

        MotorBatalla motor = crearMotor(tablero, criaturas, participantes);

        motor.iniciar();

        for (int i = 0; i < 10 && motor.estaEnEjecucion(); i++) {
            demoledor.ejecutarCiclo(motor, 100);
        }

        verificar(
                "Demoledor causa dano a Defensa 1",
                defensa1.getVidaActual() == 35
        );

        verificar(
                "Demoledor causa dano de area a Defensa 2",
                defensa2.getVidaActual() == 35
        );

        verificar(
                "Demoledor se autodestruye",
                demoledor.estaDestruido()
        );
    }

    // =========================================================
    // 20.5 VOLADOR
    // =========================================================

    private static void probarVolador() {

        System.out.println();
        System.out.println("----- 20.5 VOLADOR -----");

        ImagenesEstado imagenes = new ImagenesEstado(null, null, null);

        EstadisticasCombate statsVolador = new EstadisticasCombate(
                30, 5, 1, 1, 0, 0,
                false, 1, 1, 500
        );

        EstadisticasCombate statsNoAerea = new EstadisticasCombate(
                50, 5, 1, 5, 0, 5,
                false, 1, 1, 0
        );

        EstadisticasCombate statsAerea = new EstadisticasCombate(
                50, 5, 1, 5, 0, 5,
                true, 1, 1, 0
        );

        NucleoOxigeno nucleo = crearNucleo(imagenes);
        Tablero tablero = new Tablero(nucleo);

        Volador volador = new Volador(
                "VOL-01",
                "Volador",
                statsVolador,
                imagenes,
                1,
                new Posicion(5, 5)
        );

        DefensaContacto noAerea = new DefensaContacto(
                "NO-AIRE",
                "Defensa No Aerea",
                statsNoAerea,
                imagenes,
                1,
                new Posicion(5, 7)
        );

        DefensaContacto aerea = new DefensaContacto(
                "AIRE",
                "Defensa Aerea",
                statsAerea,
                imagenes,
                1,
                new Posicion(7, 5)
        );

        tablero.colocar(volador, new Posicion(5, 5));
        tablero.colocar(noAerea, new Posicion(5, 7));
        tablero.colocar(aerea, new Posicion(7, 5));

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(volador);

        List<ComponenteCombate> participantes = new ArrayList<>();
        participantes.add(nucleo);
        participantes.add(noAerea);
        participantes.add(aerea);
        participantes.add(volador);

        MotorBatalla motor = crearMotor(tablero, criaturas, participantes);

        motor.iniciar();

        List<ComponenteCombate> objetivosNoAereos =
                motor.buscarObjetivos(noAerea);

        List<ComponenteCombate> objetivosAereos =
                motor.buscarObjetivos(aerea);

        verificar(
                "Volador reporta esAereo() = true",
                volador.esAereo()
        );

        verificar(
                "Defensa sin capacidad aerea NO selecciona Volador",
                !objetivosNoAereos.contains(volador)
        );

        verificar(
                "Defensa con capacidad aerea SI selecciona Volador",
                objetivosAereos.contains(volador)
        );

        Posicion antes = volador.getPosicion();

        volador.ejecutarCiclo(motor, 100);

        verificar(
                "Volador puede desplazarse",
                !volador.getPosicion().equals(antes)
        );

        motor.detener();
    }

    // =========================================================
    // 20.6 ENJAMBRE
    // =========================================================

    private static void probarEnjambre() {

        System.out.println();
        System.out.println("----- 20.6 ENJAMBRE -----");

        ImagenesEstado imagenes = new ImagenesEstado(null, null, null);

        EstadisticasCombate statsEnjambre = new EstadisticasCombate(
                40, 5, 1, 3, 0, 0,
                false, 4, 2, 500
        );

        EstadisticasCombate statsDefensa = new EstadisticasCombate(
                50, 1, 1, 1, 0, 5,
                false, 1, 1, 0
        );

        NucleoOxigeno nucleo = crearNucleo(imagenes);
        Tablero tablero = new Tablero(nucleo);

        Enjambre enjambre = new Enjambre(
                "ENJ-01",
                "Enjambre",
                statsEnjambre,
                imagenes,
                1,
                new Posicion(2, 2)
        );

        DefensaContacto defensa1 = new DefensaContacto(
                "DEF-01",
                "Defensa 1",
                statsDefensa,
                imagenes,
                1,
                new Posicion(2, 3)
        );

        DefensaContacto defensa2 = new DefensaContacto(
                "DEF-02",
                "Defensa 2",
                statsDefensa,
                imagenes,
                1,
                new Posicion(3, 2)
        );

        tablero.colocar(enjambre, new Posicion(2, 2));
        tablero.colocar(defensa1, new Posicion(2, 3));
        tablero.colocar(defensa2, new Posicion(3, 2));

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(enjambre);

        List<ComponenteCombate> participantes = new ArrayList<>();
        participantes.add(nucleo);
        participantes.add(defensa1);
        participantes.add(defensa2);
        participantes.add(enjambre);

        MotorBatalla motor = crearMotor(tablero, criaturas, participantes);

        motor.iniciar();

        enjambre.ejecutarCiclo(motor, 100);

        verificar(
                "Enjambre realiza ataques multiples sobre Defensa 1",
                defensa1.getVidaActual() == 40
        );

        verificar(
                "Enjambre realiza ataques multiples sobre Defensa 2",
                defensa2.getVidaActual() == 40
        );

        motor.detener();
    }

    // =========================================================
    // METODOS DE APOYO
    // =========================================================

    private static NucleoOxigeno crearNucleo(ImagenesEstado imagenes) {

        return new NucleoOxigeno(
                "NUCLEO-01",
                "Nucleo",
                200,
                imagenes,
                new Posicion(12, 12)
        );
    }

    private static MotorBatalla crearMotor(
            Tablero tablero,
            List<Criatura> criaturas,
            List<ComponenteCombate> participantes) {

        Mision mision = new Mision(
                1,
                20,
                criaturas,
                participantes,
                true
        );

        Escuadron escuadron = new Escuadron();

        Partida partida = new Partida(
                "Prueba",
                escuadron,
                tablero,
                mision
        );

        return new MotorBatalla(partida);
    }

    private static void verificar(String nombre, boolean resultado) {

        pruebas++;

        if (resultado) {
            correctas++;
            System.out.println("[OK] " + nombre);
        } else {
            System.out.println("[ERROR] " + nombre);
        }
    }
}