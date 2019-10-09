using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
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
    private const string NUM_DOCUMENT_ELEMENT = "Número de documento generado: ";
    private readonly ILogger<WareHouseController> _logger;
    private readonly IWareHouseServices _IWareHouseServices;
    public WareHouseController(IWareHouseServices wareHouseServices, ILogger<WareHouseController> logger)
    {
      _IWareHouseServices = wareHouseServices;
      _logger = logger;
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
        _logger.LogInformation("Principal.Post: " + JsonConvert.SerializeObject(materialViewModel));
        var result = await _IWareHouseServices.CreateMaterialWarehouse(materialViewModel, warehouseid);
        return Ok(NUM_DOCUMENT_ELEMENT + result.ToString());
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