using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Interfaces
{
  public interface IWareHouseServices
  {
    Task<List<WareHouseViewModel>> GetListWareHouseByUser(string userName, int companyId);
  }
}
