using System.Collections.Generic;
using System.Threading.Tasks;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;

namespace Rcn.Bodegas.Core.Services
{
  public class CompanyServices : ICompanyServices
  {
    /// <summary>
    /// Get company list filtered by user
    /// </summary>
    /// <param name="userName"></param>
    /// <returns></returns>
    public async Task<List<CompanyViewModel>> GetCompanyByUser(string userName)
    {
      List<CompanyViewModel> result = new List<CompanyViewModel>();

      for (int i = 1; i < 21; i++)
      {
        result.Add(new CompanyViewModel
        {
          Id = i,
          CompanyName = "Company name " + i
        });
      }
      return result;
    }
  }
}
