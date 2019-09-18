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
    [HttpGet("/Inventory/GetListResponsable/{warehouse=''}/{production=''}")]
    public async Task<IActionResult> GetListResponsableAsync(string warehouse, string production)
    {
      try
      {
        var result = await _IInventroy.GetListResponsible(warehouse,production);
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

    [HttpGet("/Inventory/GetListTypeElement/{warehouseid}")]
    public async Task<IActionResult> GetListTypeElementAsync(string warehouseid)
    {
      try
      {
        var result = await _IInventroy.GetListTipoElemento(warehouseid);
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

    [HttpGet("/Inventory/GetMaterialByBarcode/{barcode}")]
    public async Task<IActionResult> GetMaterialByBarcodeAsync(string barcode)
    {
      try
      {
        var result = await _IInventroy.GetMaterialByBarCode(barcode);
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

    [HttpGet("/Inventory/GetMaterialByProduction/{wareHouseType}/{production}/{responsible}/{type_element}")]
    public async Task<IActionResult> GetMaterialByBarcodeAsync(string wareHouseType ,int production, int responsible, int type_element)
    {
      try
      {
        var result = await _IInventroy.GetMaterialsForProduction(wareHouseType,production,responsible, type_element);
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