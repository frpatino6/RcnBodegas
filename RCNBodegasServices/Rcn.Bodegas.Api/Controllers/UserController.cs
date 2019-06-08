using Microsoft.AspNetCore.Mvc;
using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using System;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Api.Controllers
{
  [Produces("application/json")]
  public class UserController : Controller
  {
    private readonly ILoginServices _ILoginServices;
    public UserController(ILoginServices loginServices)
    {
      _ILoginServices = loginServices;
    }


    [HttpGet("/User/LoginActiveDirectory/{userName=0}/{pws=''}")]
    public async Task<IActionResult> GetUserByUserName(string userName,string pws)
    {
      try
      {
        var result = _ILoginServices.GetUserAsync(userName);
        return Ok(result.Result);
      }

      catch (WareHouseExceptions ex)
      {
        return BadRequest(ex.Message);
      }
      catch (Exception ex)
      {

        return BadRequest(ex.Message);
      }
    }


    [HttpGet("/User/GetUserByUserName/{userName=0}")]
    public async Task<IActionResult> GetUserByUserName(string userName)
    {
      try
      {
        var result = _ILoginServices.GetUserAsync(userName);
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