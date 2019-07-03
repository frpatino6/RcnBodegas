using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System.Collections.Generic;
using System.Data;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Services
{
  public class InventroyService : IInventroyService
  {
    private readonly IOracleManagment _IOracleManagment;

    public InventroyService(IOracleManagment oracleManagment)
    {
      _IOracleManagment = oracleManagment;
    }

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
    public async Task<List<TipoElementoViewModel>> GetListTipoElemento()
    {
      List<TipoElementoViewModel> result = new List<TipoElementoViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = @"select * from V_TIPO_ELEMENTO ORDER BY TIPO_ELEMENTO";

      var records = _IOracleManagment.GetData(null, query);

      foreach (IDataRecord rec in records)
      {

        int id = rec.GetInt32(rec.GetOrdinal("CODIGO_TIPO_ELEMENTO"));
        string name = rec.GetString(rec.GetOrdinal("TIPO_ELEMENTO"));

        result.Add(new TipoElementoViewModel
        {
          Id = id,
          Name = name
        });
      }

      return result;
    }

    public async Task<MaterialViewModel> GetMaterialByBarCode(string barcode)
    {
      MaterialViewModel result =null;
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
        string marca = string.Empty;
        string materialName = string.Empty;
        string typeElement = string.Empty;

        if (!rec.IsDBNull(rec.GetOrdinal("MATERIAL")))
          materialName = rec.GetString(rec.GetOrdinal("MATERIAL"));

        if (!rec.IsDBNull(rec.GetOrdinal("TIPO_ELEMENTO")))
          typeElement = rec.GetString(rec.GetOrdinal("TIPO_ELEMENTO"));

        if (!rec.IsDBNull(rec.GetOrdinal("MARCA")))
          marca = rec.GetString(rec.GetOrdinal("MARCA"));

        result = new MaterialViewModel
        {
          MaterialName = materialName,
          TypeElementName = typeElement,
          Marca = marca
        };
      }

      return result;
    }
  }
}
