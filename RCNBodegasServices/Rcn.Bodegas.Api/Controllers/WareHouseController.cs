using Microsoft.AspNetCore.Mvc;
using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
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

    [HttpGet("/WareHouse/AddElementByDocument/{userName=0}/{companyId=0}")]
    public async Task<IActionResult> AddElementByDocument(string userName, int companyId)
    {
      try
      {
        var result = _IWareHouseServices.GetListWareHouseByUser(userName, companyId);
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
    [HttpPost("/WareHouse/CreateElement/{warehouseid}")]
    public async Task<IActionResult> CreateElementAsync([FromBody] List<MaterialViewModel> materialViewModel, string warehouseid)
    {
      try
      {
        var result = _IWareHouseServices.CreateMaterialWarehouse(materialViewModel, warehouseid);
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



  }
}