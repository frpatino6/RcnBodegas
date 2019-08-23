﻿using Microsoft.AspNetCore.Mvc;
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
    public async Task<IActionResult> GetUserByUserName(string userName, string pws)
    {
      try
      {
        var result = await _ILoginServices.GetUserAsync(userName, pws);
        return Ok(result);
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


    [HttpGet("/User/GetUserByUserName/{userName}/{password}")]
    public async Task<IActionResult> GetUserByUserNameasync(string userName, string password)
    {
      try
      {
        var result = await _ILoginServices.GetUserAsync(userName, password);
        return Ok(result);
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

  }
}