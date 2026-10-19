package com.fundacioncana.dao;

import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.modelo.Entidad;

import java.util.List;
import java.util.Optional;

/**
 * Contrato común de acceso a datos. Cada DAO lo implementa para su entidad,
 * de modo que los controladores dependen de la interfaz y no de las consultas SQL.
 */
public interface CrudDao<T extends Entidad> {

    List<T> listar() throws PersistenciaException;

    Optional<T> buscarPorId(int id) throws PersistenciaException;

    void insertar(T entidad) throws PersistenciaException;

    void actualizar(T entidad) throws PersistenciaException;

    void eliminar(int id) throws PersistenciaException;
}
