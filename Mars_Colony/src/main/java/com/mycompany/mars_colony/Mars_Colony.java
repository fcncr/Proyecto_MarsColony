package com.mycompany.mars_colony;

import com.mycompany.mars_colony.motor.HiloUnidad;
import com.mycompany.mars_colony.motor.MotorBatalla;

import com.mycompany.mars_colony.modelo.combate.ComponenteCombate;
import com.mycompany.mars_colony.modelo.combate.Criatura;
import com.mycompany.mars_colony.modelo.combate.Defensa;

import com.mycompany.mars_colony.modelo.combate.criatura.Acechador;
import com.mycompany.mars_colony.modelo.combate.defensa.DefensaAlcance;

import com.mycompany.mars_colony.modelo.estado.EstadisticasCombate;
import com.mycompany.mars_colony.modelo.estado.ImagenesEstado;

import com.mycompany.mars_colony.modelo.mapa.NucleoOxigeno;
import com.mycompany.mars_colony.modelo.mapa.Obstaculo;
import com.mycompany.mars_colony.modelo.mapa.OcupanteMapa;
import com.mycompany.mars_colony.modelo.mapa.Posicion;
import com.mycompany.mars_colony.modelo.mapa.Tablero;

import com.mycompany.mars_colony.modelo.partida.Escuadron;
import com.mycompany.mars_colony.modelo.partida.EstadoMision;
import com.mycompany.mars_colony.modelo.partida.Mision;
import com.mycompany.mars_colony.modelo.partida.Partida;

import com.mycompany.mars_colony.modelo.registro.RegistroCrecimiento;
import com.mycompany.mars_colony.modelo.registro.ResumenInteraccion;

import com.mycompany.mars_colony.persistencia.RepositorioPartidas;

import com.mycompany.mars_colony.servicio.ServicioCampania;
import com.mycompany.mars_colony.servicio.ServicioProgresion;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Mars_Colony {

    public static void main(String[] args) {
    }
}