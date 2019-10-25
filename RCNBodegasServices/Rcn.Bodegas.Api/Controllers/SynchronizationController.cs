using Microsoft.AspNetCore.Mvc;
using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Api.Controllers
{
  [Produces("application/json")]
  public class SynchronizationController : Controller
  {
    private readonly ISyncronization _ISyncronization;

    public SynchronizationController(ISyncronization  syncronization)
    {
      this._ISyncronization = syncronization;
    }
    [HttpGet("/sync/GetListAllWarehouse/")]
    public async Task<IActionResult> GetListAllWarehouse()
    {
      try
      {
        var result = await _ISyncronization.GetListAllWarehouseUserAsync();
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

    [HttpGet("/sync/GetAllListProductions/")]
    public async Task<IActionResult> GetAllListProductionsAsync()
    {
      try
      {
        var result = await _ISyncronization.GetAllListProductionsAsync();
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

    [HttpGet("/sync/GetAllListTipoElemento/")]
    public async Task<IActionResult> GetAllListTipoElementoAsync()
    {
      try
      {
        var result = await _ISyncronization.GetAllListTipoElementoAsync();
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

    [HttpGet("/sync/GetAllListTipoPrenda/")]
    public async Task<IActionResult> GetAllListTipoPrendaAsync()
    {
      try
      {
        var result = await _ISyncronization.GetAllListTipoPrendaAsync();
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

    [HttpGet("/sync/GetListAllResponsible/")]
    public async Task<IActionResult> GetListAllResponsibleAsync()
    {
      try
      {
        var result = await _ISyncronization.GetListAllResponsibleAsync();
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

    [HttpGet("/sync/GetListAllMaterial/{OffSet}")]
    public async Task<IActionResult> GetListAllMaterialAsync(Int64 OffSet)
    {
      try
      {
        var result = await _ISyncronization.GetListAllMaterialAsync(OffSet);
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

    [HttpGet("/sync/GetListAllWarehouseUser/")]
    public async Task<IActionResult> GetListAllWarehouseUserAsync()
    {
      try
      {
        var result = await _ISyncronization.GetAllListWarehouseUserAsync();
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

    [HttpGet("/sync/GetCountMateriales/")]
    public async Task<IActionResult> GetCountMaterialesAsync()
    {
      try
      {
        var result = await _ISyncronization.GetCountMaterialAsync();
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
