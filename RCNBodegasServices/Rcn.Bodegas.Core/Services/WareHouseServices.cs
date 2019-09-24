﻿using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Drawing;
using System.IO;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Services
{
  public class WareHouseServices : IWareHouseServices
  {
    private readonly IOracleManagment _IOracleManagment;

    public WareHouseServices(IOracleManagment oracleManagment)
    {
      _IOracleManagment = oracleManagment;
    }

    public async Task<string> CreateMaterialWarehouse(List<MaterialViewModel> listNewMaterial, string warehouseid)
    {
      //Crear registro del movimiento
      //if (GenerateMovement() == 1)
      // crea el registro en la tabla material


      return InsertMaterial(listNewMaterial, warehouseid).ToString();

    }

    private int InsertMaterial(List<MaterialViewModel> listNewMaterial, string warehouseid)
    {
      string query;
      string dateLegalization;
      int rowCount = 0;
      int numeroRecepcion = 0;
      OracleParameter OraBarCode = new OracleParameter(":CODIGO_BARRAS", OracleDbType.Varchar2, 20, ParameterDirection.Input);
      OracleParameter OraDescripcion = new OracleParameter(":DESCRIPCION", OracleDbType.Varchar2, 2000, ParameterDirection.Input);
      OracleParameter OraTipoBodega = new OracleParameter(":TIPO_BODEGA", OracleDbType.Varchar2, 1, ParameterDirection.Input);
      OracleParameter OraUbicacionActua = new OracleParameter(":BD_UBCCION_ACTUAL", OracleDbType.Int32, ParameterDirection.Input);
      OracleParameter OraEstado = new OracleParameter(":ESTADO", OracleDbType.Varchar2, 1, ParameterDirection.Input);
      OracleParameter OraEmpresaCodigo = new OracleParameter(":AD_EMPRESA_CODIGO", OracleDbType.Int32, ParameterDirection.Input);
      OracleParameter OraUbicacionCodigo = new OracleParameter(":BD_UBCCION_CODIGO", OracleDbType.Int32, ParameterDirection.Input);
      OracleParameter OraUsuarioCreacion = new OracleParameter(":USU_CREACION", OracleDbType.Varchar2, 20, ParameterDirection.Input);
      OracleParameter OraTerceroActual = new OracleParameter(":PA_TERCERO_ACTUAL", OracleDbType.Int64, ParameterDirection.Input);
      OracleParameter OraValorCompra = new OracleParameter(":VALOR_COMPRA", OracleDbType.Decimal, ParameterDirection.Input);
      OracleParameter OraValorMateria = new OracleParameter(":VALOR_MATERIA", OracleDbType.Decimal, ParameterDirection.Input);
      OracleParameter OraElementType = new OracleParameter(":TIPO_ELEMENTO", OracleDbType.Int32, ParameterDirection.Input);
      OracleParameter OraPrendaType = new OracleParameter(":TIPO_PRENDA", OracleDbType.Int32, ParameterDirection.Input);
      OracleParameter OraMarca = new OracleParameter(":MARCA", OracleDbType.Varchar2, 48, ParameterDirection.Input);
      OracleParameter OraUbicacionRecibido = new OracleParameter(":BD_UBICACION_RECIBIDO", OracleDbType.Varchar2, 48, ParameterDirection.Input);
      OracleParameter OraTerceroRecibido = new OracleParameter(":PA_TERCERO_RECIBIDO", OracleDbType.Int32, ParameterDirection.Input);
      OracleParameter OraFechaCompra = new OracleParameter(":FECHA_COMPRA", OracleDbType.Date, ParameterDirection.Input);


      OracleParameter OraCodigoElemento = new OracleParameter("new_id", OracleDbType.Int32);
      OraCodigoElemento.Direction = ParameterDirection.Output;

      numeroRecepcion = getDocumentNumber(warehouseid);
      dateLegalization = listNewMaterial[0].saleDate.ToString("yyyy-MM-dd");
      if (warehouseid.Equals("V"))
      {
        query = $@"Insert into BD_MATERIAL (CODIGO,CODIGO_BARRAS,DESCRIPCION,TIPO_BODEGA,BD_UBCCION_ACTUAL,FEC_CREACION,ESTADO,AD_EMPRESA_CODIGO,BD_UBCCION_CODIGO,USU_CREACION,VALOR_COMPRA,VALOR_MATERIA,PA_TERCERO_ACTUAL,TIPO_PRENDA,MARCA,VALOR_RETENCION,ASIGNACION_BARRAS,BD_UBICACION_RECIBIDO,PA_TERCERO_RECIBIDO,FECHA_COMPRA,INDICADOR_SALIDA,NUMERO_DOC_RECEPCION)
                 VALUES(BD_MTRIAL_SEQ.nextval, :CODIGO_BARRAS, :DESCRIPCION, :TIPO_BODEGA, :BD_UBCCION_ACTUAL, SYSDATE, :ESTADO, :AD_EMPRESA_CODIGO, :BD_UBCCION_CODIGO, :USU_CREACION, :VALOR_COMPRA, :VALOR_MATERIA,42877557,:TIPO_ELEMENTO,:MARCA,0,'S',:BD_UBICACION_RECIBIDO,:PA_TERCERO_RECIBIDO,TO_DATE('" + dateLegalization + "', 'YYYY-MM-DD HH:mi:ss'),'X'," + numeroRecepcion + ") returning CODIGO into :new_id";
      }
      else
      {
        query = $@"Insert into BD_MATERIAL (CODIGO,CODIGO_BARRAS,DESCRIPCION,TIPO_BODEGA,BD_UBCCION_ACTUAL,FEC_CREACION,ESTADO,AD_EMPRESA_CODIGO,BD_UBCCION_CODIGO,USU_CREACION,VALOR_COMPRA,VALOR_MATERIA,PA_TERCERO_ACTUAL,TIPO_ELEMENTO,MARCA,VALOR_RETENCION,ASIGNACION_BARRAS,BD_UBICACION_RECIBIDO,PA_TERCERO_RECIBIDO,FECHA_COMPRA,INDICADOR_SALIDA,NUMERO_DOC_RECEPCION)
                 VALUES(BD_MTRIAL_SEQ.nextval, :CODIGO_BARRAS, :DESCRIPCION, :TIPO_BODEGA, :BD_UBCCION_ACTUAL, SYSDATE, :ESTADO, :AD_EMPRESA_CODIGO, :BD_UBCCION_CODIGO, :USU_CREACION, :VALOR_COMPRA, :VALOR_MATERIA,42877557,:TIPO_ELEMENTO,:MARCA,0,'S',:BD_UBICACION_RECIBIDO,:PA_TERCERO_RECIBIDO,TO_DATE('" + dateLegalization + "', 'YYYY-MM-DD HH:mi:ss'),'X'," + numeroRecepcion + ") returning CODIGO into :new_id";
      }

      using (OracleConnection con = new OracleConnection(_IOracleManagment.GetOracleConnectionParameters()))
      {
        con.Open();
        using (OracleCommand oraUpdate = con.CreateCommand())
        {
          using (OracleTransaction transaction = con.BeginTransaction(IsolationLevel.ReadCommitted))
          {
            oraUpdate.Transaction = transaction;
            try
            {
              //Busca el numero de documento con el cual se creo el material en la base de datos


              oraUpdate.CommandText = query;
              oraUpdate.Parameters.Clear();
              oraUpdate.Parameters.Add(OraBarCode);
              oraUpdate.Parameters.Add(OraDescripcion);
              oraUpdate.Parameters.Add(OraTipoBodega);
              oraUpdate.Parameters.Add(OraUbicacionActua);
              oraUpdate.Parameters.Add(OraEstado);
              oraUpdate.Parameters.Add(OraEmpresaCodigo);
              oraUpdate.Parameters.Add(OraUbicacionCodigo);
              oraUpdate.Parameters.Add(OraUsuarioCreacion);
              oraUpdate.Parameters.Add(OraValorCompra);
              oraUpdate.Parameters.Add(OraValorMateria);
              oraUpdate.Parameters.Add(OraElementType);
              oraUpdate.Parameters.Add(OraMarca);
              oraUpdate.Parameters.Add(OraUbicacionRecibido);
              oraUpdate.Parameters.Add(OraTerceroRecibido);
              oraUpdate.Parameters.Add(OraCodigoElemento);
             // oraUpdate.Parameters.Add(OraFechaCompra);              

              if (numeroRecepcion > 0)
                foreach (var newMaterial in listNewMaterial)
                {
                  OraBarCode.Value = newMaterial.barCode;
                  OraDescripcion.Value = newMaterial.materialName;
                  OraTipoBodega.Value = newMaterial.wareHouseId;
                  OraUbicacionActua.Value = 1;
                  OraEstado.Value = "A";
                  OraEmpresaCodigo.Value = 1;
                  OraUbicacionCodigo.Value = 1;
                  OraUsuarioCreacion.Value = "BODEGAS";
                  // OraTerceroActual.Value = "42877557"; //TODO: CEDULA DEL USUARIO LOGEADO
                  OraValorCompra.Value = newMaterial.unitPrice;
                  OraValorMateria.Value = newMaterial.purchaseValue;
                  OraElementType.Value = newMaterial.typeElementId;
                  OraMarca.Value = newMaterial.marca;
                  OraUbicacionRecibido.Value = newMaterial.productionId;
                  OraTerceroRecibido.Value = newMaterial.legalizedBy;
                  //OraFechaCompra.Value = newMaterial.saleDate.ToShortDateString();
                  rowCount = oraUpdate.ExecuteNonQuery();

                  if (newMaterial.ListaImagenesStr != null && newMaterial.ListaImagenesStr.Count > 0)
                    InsertImagesByMaterial(OraCodigoElemento.Value.ToString(), newMaterial.ListaImagenesStr, con, transaction);
                }
              transaction.Commit();


            }
            catch (System.Exception ex)
            {
              transaction.Rollback();
              throw new System.Exception(ex.Message);
            }
          }
        }
      }
      return numeroRecepcion;
    }

    private void InsertImagesByMaterial(string codigoMaterial, List<string> listImages, OracleConnection con, OracleTransaction transaction)
    {
      int rowCount = 0;
      OracleParameter OraEmpresae = new OracleParameter(":AD_EMPRESA_CODIGO", OracleDbType.Int32, 20, ParameterDirection.Input);
      OracleParameter OraMaterialCodigo = new OracleParameter(":BD_MATERIAL_CODIGO", OracleDbType.Int32, 2000, ParameterDirection.Input);
      OracleParameter OraConsecutivo = new OracleParameter(":CONSECUTIVO", OracleDbType.Int32, 1, ParameterDirection.Input);
      OracleParameter OraDescripcion = new OracleParameter(":DESCRIPCION", OracleDbType.Varchar2, ParameterDirection.Input);
      OracleParameter OraArchivo = new OracleParameter(":ARCHIVO", OracleDbType.Varchar2, 1, ParameterDirection.Input);
      OracleParameter OraFoto = new OracleParameter(":FOTO", OracleDbType.Blob, ParameterDirection.Input);

      string query = $@" INSERT INTO BD_IMAGENES (AD_EMPRESA_CODIGO,BD_MATERIAL_CODIGO,CONSECUTIVO,DESCRIPCION,ARCHIVO,FOTO,FOTO_JAVA)
                                          VALUES(:AD_EMPRESA_CODIGO,:BD_MATERIAL_CODIGO,:CONSECUTIVO,:DESCRIPCION,:ARCHIVO,:FOTO,:FOTO)";

      OracleCommand oraUpdate = con.CreateCommand();
      int consecutivo = 1;

      oraUpdate.CommandText = query;
      oraUpdate.Parameters.Add(OraEmpresae);
      oraUpdate.Parameters.Add(OraMaterialCodigo);
      oraUpdate.Parameters.Add(OraConsecutivo);
      oraUpdate.Parameters.Add(OraDescripcion);
      oraUpdate.Parameters.Add(OraArchivo);
      oraUpdate.Parameters.Add(OraFoto);

      Image imageForSave;

      foreach (var item in listImages)
      {

        byte[] bytesImage = Convert.FromBase64String(item);

        using (MemoryStream mStream = new MemoryStream(bytesImage))
        {
          imageForSave = Image.FromStream(mStream);
        }


        OraEmpresae.Value = 1;
        OraMaterialCodigo.Value = codigoMaterial;
        OraConsecutivo.Value = consecutivo;
        OraDescripcion.Value = string.Empty;
        OraArchivo.Value = string.Empty;//ESTE CAMPO SE LLENA CON EL CÓDIGO DE BARRAS MÁS LA EXTENSIÓN DEL ARCHIVO
        OraFoto.Value = bytesImage;
        consecutivo++;
        rowCount = oraUpdate.ExecuteNonQuery();
      }


    }

    private int getDocumentNumber(string tipoBodega)
    {
      int id = 0;
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = @"SELECT NVL(MAX(numero_doc_recepcion),0) + 1 as NUMERO_DOC
                    FROM bd_material
                    WHERE tipo_bodega = :TIPO_BODEGA";

      OracleParameter opCodigoMaterial = new OracleParameter();
      opCodigoMaterial.DbType = DbType.String;
      opCodigoMaterial.Value = tipoBodega;
      opCodigoMaterial.ParameterName = ":TIPO_BODEGA";
      parameters.Add(opCodigoMaterial);


      var records = _IOracleManagment.GetData(parameters, query);

      foreach (IDataRecord rec in records)
      {
        id = rec.GetInt32(rec.GetOrdinal("NUMERO_DOC"));
      }
      return id;
    }
    public async Task<List<WareHouseViewModel>> GetListWareHouseByUser(string userName, int companyId)
    {
      List<WareHouseViewModel> result = new List<WareHouseViewModel>();
      var query = @"select * from V_TIPO_BODEGA";
      var records = _IOracleManagment.GetData(null, query);

      foreach (IDataRecord rec in records)
      {
        string tipo_bodega = rec.GetString(rec.GetOrdinal("TIPO_BODEGA"));
        string id = rec.GetString(rec.GetOrdinal("CODIGO"));
        result.Add(new WareHouseViewModel
        {
          Id = id,
          WareHouseName = tipo_bodega
        });
      }

      return result;

    }
  }
}