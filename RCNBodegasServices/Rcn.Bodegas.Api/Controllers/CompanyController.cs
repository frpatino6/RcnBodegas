using Microsoft.AspNetCore.Mvc;
using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using System;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Api.Controllers
{
  [Produces("application/json")]
  public class CompanyController : Controller
  {
    private readonly ICompanyServices _ICompanyServices;
    public CompanyController(ICompanyServices companyServices)
    {
      _ICompanyServices = companyServices;
    }

    [HttpGet("/Company/GetLisCompany/{userName=0}")]
    public async Task<IActionResult> GetLisWareHouseAsync(string userName)
    {
      try
      {
        var result = _ICompanyServices.GetCompanyByUser(userName);
        return Ok(result.Result);
      }

      catch (WareHouseExceptions ex) {
        return BadRequest(ex.Message);
      }
      catch (Exception ex)
      {

        return BadRequest(ex.Message);
      }
    }

  }
}