using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System.Collections.Generic;
using System.Data;
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
