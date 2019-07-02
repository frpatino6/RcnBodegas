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

    public async  Task<List<ProductionViewModel>> GetListProductions(string wareHouse)
    {
      List<ProductionViewModel> result = new List<ProductionViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = @"select * from V_PRODUCCION WHERE CODIGO_TIPO_BODEGA=:wharehouse ORDER BY PRODUCCION";

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
          InternalOrder=string.Empty,
          NameWareHouseType=name,
          ProductionCode=cod,
          ProductionName=prod
        });
      }

      return result;
    }

    public async Task<List<ResponsibleViewModel>> GetListResponsible(string production)
    {
      List<ResponsibleViewModel> result = new List<ResponsibleViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = @"select * from V_MATERIALES WHERE CODIGO_PRODUCCION=:production";

      OracleParameter opwareHouse = new OracleParameter();
      opwareHouse.DbType = DbType.String;
      opwareHouse.Value = production;
      opwareHouse.ParameterName = "production";
      parameters.Add(opwareHouse);


      var records = _IOracleManagment.GetData(parameters, query);

      foreach (IDataRecord rec in records)
      {

        int id = rec.GetInt32(rec.GetOrdinal("CODIGO_RESPONSABLE"));
        string name = rec.GetString(rec.GetOrdinal("NOMBRE_RESPONSABLE"));
    


        result.Add(new ResponsibleViewModel
        {
          Id = id,
          Name=name
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

      return  result;
    }
  }
}
