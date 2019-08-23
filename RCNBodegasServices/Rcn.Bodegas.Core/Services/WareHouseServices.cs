using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
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


      int cod_material = InsertMaterial(listNewMaterial, warehouseid);



      return string.Empty;
    }

    private int InsertMaterial(List<MaterialViewModel> listNewMaterial, string warehouseid)
    {
      string query;
      int rowCount = 0;
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
      OracleParameter OraElementType = new OracleParameter(":TIPO_ELEMENTO", OracleDbType.Int32, ParameterDirection.Input);
      OracleParameter OraPrendaType = new OracleParameter(":TIPO_PRENDA", OracleDbType.Int32, ParameterDirection.Input);

      OracleParameter OraCodigoElemento = new OracleParameter("new_id", OracleDbType.Int32);
      OraCodigoElemento.Direction = ParameterDirection.Output;



      if (warehouseid.Equals("V"))
      {
        query = $@"Insert into BD_MATERIAL (CODIGO,CODIGO_BARRAS,DESCRIPCION,TIPO_BODEGA,BD_UBCCION_ACTUAL,FEC_CREACION,ESTADO,AD_EMPRESA_CODIGO,BD_UBCCION_CODIGO,USU_CREACION,VALOR_COMPRA,PA_TERCERO_ACTUAL,TIPO_PRENDA)
                 VALUES(BD_MTRIAL_SEQ.nextval, :CODIGO_BARRAS, :DESCRIPCION, :TIPO_BODEGA, :BD_UBCCION_ACTUAL, SYSDATE, :ESTADO, :AD_EMPRESA_CODIGO, :BD_UBCCION_CODIGO, :USU_CREACION, :VALOR_COMPRA, 42877557,:TIPO_ELEMENTO) returning CODIGO into :new_id";
      }
      else
      {
        query = $@"Insert into BD_MATERIAL (CODIGO,CODIGO_BARRAS,DESCRIPCION,TIPO_BODEGA,BD_UBCCION_ACTUAL,FEC_CREACION,ESTADO,AD_EMPRESA_CODIGO,BD_UBCCION_CODIGO,USU_CREACION,VALOR_COMPRA,PA_TERCERO_ACTUAL,TIPO_ELEMENTO)
                 VALUES(BD_MTRIAL_SEQ.nextval, :CODIGO_BARRAS, :DESCRIPCION, :TIPO_BODEGA, :BD_UBCCION_ACTUAL, SYSDATE, :ESTADO, :AD_EMPRESA_CODIGO, :BD_UBCCION_CODIGO, :USU_CREACION, :VALOR_COMPRA, 42877557,:TIPO_ELEMENTO) returning CODIGO into :new_id";
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
              oraUpdate.Parameters.Add(OraElementType);
              oraUpdate.Parameters.Add(OraCodigoElemento);
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
                OraTerceroActual.Value = "42877557"; //TODO: CEDULA DEL USUARIO LOGEADO
                OraValorCompra.Value = newMaterial.unitPrice;
                OraElementType.Value = newMaterial.typeElementId;

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
      return rowCount;
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

        byte[] bytesImage =Convert.FromBase64String(item);

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