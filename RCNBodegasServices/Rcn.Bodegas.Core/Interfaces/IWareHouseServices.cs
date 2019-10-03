using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.ViewModel;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Interfaces
{
  public interface IWareHouseServices
  {
    Task<List<WareHouseViewModel>> GetListWareHouseByUser(string userName, int companyId);

    Task<int> CreateMaterialWarehouse(List<MaterialViewModel> newMaterial, string warehouseid);

    
  }
}
