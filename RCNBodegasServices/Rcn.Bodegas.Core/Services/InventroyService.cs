﻿using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System.Collections.Generic;
using System.Data;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Services
{
  public class InventroyService : IInventroyService
  {
    private const string WAREHOUSE_TYPE_V = "V";
    private readonly IOracleManagment _IOracleManagment;

    public InventroyService(IOracleManagment oracleManagment)
    {
      _IOracleManagment = oracleManagment;
    }

    /// <summary>
    /// Get list productions
    /// </summary>
    /// <param name="wareHouse"></param>
    /// <returns></returns>
    public async Task<List<ProductionViewModel>> GetListProductions(string wareHouse)
    {
      List<ProductionViewModel> result = new List<ProductionViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = @"select * from V_PRODUCCION  ORDER BY PRODUCCION";

      OracleParameter opwareHouse = new OracleParameter();
      opwareHouse.DbType = DbType.String;
      opwareHouse.Value = wareHouse;
      opwareHouse.ParameterName = "wharehouse";
      parameters.Add(opwareHouse);


      var records = _IOracleManagment.GetData(parameters, query);

      foreach (IDataRecord rec in records)
      {

        string id = rec.GetString(rec.GetOrdinal("CODIGO_TIPO_BODEGA"));
        string name = rec.GetString(rec.GetOrdinal("NOMBRE_TIPO_BODEGA"));
        int cod = rec.GetInt32(rec.GetOrdinal("CODIGO_PRODUCCION"));
        string prod = rec.GetString(rec.GetOrdinal("PRODUCCION"));


        result.Add(new ProductionViewModel
        {
          Id = id,
          InternalOrder = string.Empty,
          NameWareHouseType = name,
          ProductionCode = cod,
          ProductionName = prod
        });
      }

      return result;
    }

    /// <summary>
    /// Get list responsible
    /// </summary>
    /// <param name="wareHouse"></param>
    /// <param name="production"></param>
    /// <returns></returns>
    public async Task<List<ResponsibleViewModel>> GetListResponsible(string wareHouse, string production)
    {
      List<ResponsibleViewModel> result = new List<ResponsibleViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = @"select distinct CODIGO_RESPONSABLE ,NOMBRE_RESPONSABLE  
                    from V_MATERIALES 
                    WHERE CODIGO_PRODUCCION=:production AND CODIGO_TIPO_BODEGA=:warehouse
                    ORDER BY NOMBRE_RESPONSABLE";

      OracleParameter opwareHouse = new OracleParameter();
      opwareHouse.DbType = DbType.String;
      opwareHouse.Value = wareHouse;
      opwareHouse.ParameterName = "warehouse";
      parameters.Add(opwareHouse);

      OracleParameter opProduccion = new OracleParameter();
      opProduccion.DbType = DbType.String;
      opProduccion.Value = production;
      opProduccion.ParameterName = "production";
      parameters.Add(opProduccion);

      var records = _IOracleManagment.GetData(parameters, query);

      foreach (IDataRecord rec in records)
      {

        int id = rec.GetInt32(rec.GetOrdinal("CODIGO_RESPONSABLE"));
        string name = rec.GetString(rec.GetOrdinal("NOMBRE_RESPONSABLE"));



        result.Add(new ResponsibleViewModel
        {
          Id = id,
          Name = name
        });
      }

      return result;
    }

    /// <summary>
    /// Get list TIPO_ELEMENTO
    /// </summary>
    /// <returns></returns>
    public async Task<List<TipoElementoViewModel>> GetListTipoElemento(string wareHouseid)
    {
      List<TipoElementoViewModel> result = new List<TipoElementoViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      string query = string.Empty;

      if (wareHouseid.Equals(WAREHOUSE_TYPE_V))
        query = @"select CODIGO_TIPO, NOMBRE_TIPO from BD_TIPO_PRENDA ORDER BY NOMBRE_TIPO";
      else {
        query = @"select CODIGO_TIPO, NOMBRE_TIPO from BD_TIPO_ELEMENTO ORDER BY NOMBRE_TIPO";
      }

      var records = _IOracleManagment.GetData(null, query);

      foreach (IDataRecord rec in records)
      {

        int id = rec.GetInt32(rec.GetOrdinal("CODIGO_TIPO"));
        string name = rec.GetString(rec.GetOrdinal("NOMBRE_TIPO"));

        result.Add(new TipoElementoViewModel
        {
          Id = id,
          Name = name
        });
      }

      return result;
    }

    /// <summary>
    /// Get list material by barcode
    /// </summary>
    /// <param name="barcode"></param>
    /// <returns></returns>
    public async Task<MaterialViewModel> GetMaterialByBarCode(string barcode)
    {
      string marca = string.Empty;
      string materialName = string.Empty;
      string typeElement = string.Empty;

      MaterialViewModel result = null;
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = @"select * from V_MATERIALES WHERE CODIGO_BARRAS=:barcode";

      OracleParameter opBarcode = new OracleParameter();
      opBarcode.DbType = DbType.String;
      opBarcode.Value = barcode;
      opBarcode.ParameterName = "barcode";
      parameters.Add(opBarcode);

      var records = _IOracleManagment.GetData(parameters, query);

      foreach (IDataRecord rec in records)
      {


        if (!rec.IsDBNull(rec.GetOrdinal("MATERIAL")))
          materialName = rec.GetString(rec.GetOrdinal("MATERIAL"));

        if (!rec.IsDBNull(rec.GetOrdinal("TIPO_ELEMENTO")))
          typeElement = rec.GetString(rec.GetOrdinal("TIPO_ELEMENTO"));

        if (!rec.IsDBNull(rec.GetOrdinal("MARCA")))
          marca = rec.GetString(rec.GetOrdinal("MARCA"));

        result = new MaterialViewModel
        {
          materialName = materialName,
          typeElementName = typeElement,
          marca = marca
        };
      }

      return result;
    }

    /// <summary>
    /// Get list material 
    /// </summary>
    /// <param name="warehouseType"></param>
    /// <param name="idProdction"></param>
    /// <param name="idResponsible"></param>
    /// <returns></returns>
    public async Task<List<MaterialViewModel>> GetMaterialsForProduction(string warehouseType, string idProdction, int idResponsible)
    {
      string marca = string.Empty;
      string materialName = string.Empty;
      string typeElement = string.Empty;
      string barCode = string.Empty;
      decimal unitPrice=0;

      List<MaterialViewModel> result = new List<MaterialViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = @"select * from V_MATERIALES 
                  WHERE CODIGO_TIPO_BODEGA=:COD_TIPO_BODEGA AND CODIGO_PRODUCCION=:COD_PRODUCCION AND CODIGO_RESPONSABLE=:COD_RESPONSIBLE
                  ORDER BY MARCA";

      OracleParameter OpCodProduction = new OracleParameter();
      OpCodProduction.DbType = DbType.String;
      OpCodProduction.Value = idProdction;
      OpCodProduction.ParameterName = "COD_PRODUCCION";
      parameters.Add(OpCodProduction);

      OracleParameter OpwarehouseType = new OracleParameter();
      OpwarehouseType.DbType = DbType.String;
      OpwarehouseType.Value = warehouseType;
      OpwarehouseType.ParameterName = "COD_TIPO_BODEGA";
      parameters.Add(OpwarehouseType);

      OracleParameter OpCodResponsible = new OracleParameter();
      OpCodResponsible.DbType = DbType.Int64;
      OpCodResponsible.Value = idResponsible;
      OpCodResponsible.ParameterName = "COD_RESPONSIBLE";
      parameters.Add(OpCodResponsible);



      var records = _IOracleManagment.GetData(parameters, query);

      foreach (IDataRecord rec in records)
      {

        if (!rec.IsDBNull(rec.GetOrdinal("MATERIAL")))
          materialName = rec.GetString(rec.GetOrdinal("MATERIAL"));

        if (!rec.IsDBNull(rec.GetOrdinal("TIPO_ELEMENTO")))
          typeElement = rec.GetString(rec.GetOrdinal("TIPO_ELEMENTO"));

        if (!rec.IsDBNull(rec.GetOrdinal("MARCA")))
          marca = rec.GetString(rec.GetOrdinal("MARCA"));

        if (!rec.IsDBNull(rec.GetOrdinal("CODIGO_BARRAS")))
          barCode = rec.GetString(rec.GetOrdinal("CODIGO_BARRAS"));

        if (!rec.IsDBNull(rec.GetOrdinal("PRECIO_UNITARIO")))
          unitPrice = rec.GetDecimal(rec.GetOrdinal("PRECIO_UNITARIO"));

        result.Add(new MaterialViewModel 
        {
          materialName = materialName,
          typeElementName = typeElement,
          marca = marca,
          barCode = barCode,
          unitPrice=unitPrice
        
        });
    }

      return result;
    }
}
}
