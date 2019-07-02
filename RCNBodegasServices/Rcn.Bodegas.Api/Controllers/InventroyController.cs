using Microsoft.AspNetCore.Mvc;
using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using System;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Api.Controllers
{
  [Produces("application/json")]
  public class InventroyController : Controller
  {
    private readonly IInventroyService _IInventroy;
    public InventroyController(IInventroyService inventroy)
    {
      _IInventroy = inventroy;
    }

    [HttpGet("/Inventory/GetListProduction/{warehouseid=0}")]
    public async Task<IActionResult> GetLisWareHouseAsync(string warehouseid)
    {
      try
      {
        var result = await _IInventroy.GetListProductions(warehouseid);
        return Ok(result);
      }

      catch (WareHouseExceptions ex) {
        return BadRequest(ex.Message);
      }
      catch (Exception ex)
      {

        return BadRequest(ex.Message);
      }
    }
    [HttpGet("/Inventory/GetListResponsable/{production=''}")]
    public async Task<IActionResult> GetListResponsableAsync(string production)
    {
      try
      {
        var result = await _IInventroy.GetListResponsible(production);
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

    [HttpGet("/Inventory/GetListTypeElement/")]
    public async Task<IActionResult> GetListTypeElementAsync()
    {
      try
      {
        var result = await _IInventroy.GetListTipoElemento();
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