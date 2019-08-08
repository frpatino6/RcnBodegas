using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Interfaces
{
  public interface ILoginServices
  {
    Task<UserViewModel> GetUserAsync(string userName, string password);
  }
}
