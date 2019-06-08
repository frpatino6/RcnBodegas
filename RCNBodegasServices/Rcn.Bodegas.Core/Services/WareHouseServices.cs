using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Services
{
  public class WareHouseServices : IWareHouseServices
  {
    public async Task<List<WareHouseViewModel>> GetListWareHouseByUser(string userName, int companyId)
    {
      List<WareHouseViewModel> result = new List<WareHouseViewModel>();

      for (int i = 1; i < 21; i++)
      {
        result.Add(new WareHouseViewModel
        {
          Id = i,
          WareHouseName = "Bodega " + i
        });
      }

      if (result.Count == 0)
        throw new WareHouseExceptions(userName);
      else
        return result;
    }
  }
}
