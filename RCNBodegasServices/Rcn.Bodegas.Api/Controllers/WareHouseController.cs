using Microsoft.AspNetCore.Mvc;
using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using System;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Api.Controllers
{
  [Produces("application/json")]
  public class WareHouseController : Controller
  {
    private readonly IWareHouseServices _IWareHouseServices;
    public WareHouseController(IWareHouseServices wareHouseServices)
    {
      _IWareHouseServices = wareHouseServices;
    }

    [HttpGet("/WareHouse/GetLisWareHouse/{userName=0}/{companyId=0}")]
    public async Task<IActionResult> GetLisWareHouseAsync(string userName, int companyId)
    {
      try
      {
        var result = _IWareHouseServices.GetListWareHouseByUser(userName, companyId);
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