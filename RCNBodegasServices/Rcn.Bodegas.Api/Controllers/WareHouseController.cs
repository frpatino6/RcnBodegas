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
                _logger.LogInformation($@"GetLisWareHouseAsync.Get: {userName} {companyId}");
                Task<List<WareHouseViewModel>> result = _IWareHouseServices.GetListWareHouseByUser(userName, companyId);
                return Ok(result.Result);
            }

            catch (WareHouseExceptions ex)
            {
                _logger.LogError("WareHouseExceptions GetLisWareHouseAsync.Get ERROR: " + ex.Message);
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError("Exception  GetLisWareHouseAsync.Get ERROR: " + ex.Message);
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("/WareHouse/AddElementByDocument/{userName=0}/{companyId=0}")]
        public async Task<IActionResult> AddElementByDocument(string userName, int companyId)
        {
            try
            {
                _logger.LogInformation($@"AddElementByDocument.Get: {userName} {companyId}");
                Task<List<WareHouseViewModel>> result = _IWareHouseServices.GetListWareHouseByUser(userName, companyId);
                return Ok(result.Result);
            }

            catch (WareHouseExceptions ex)
            {
                _logger.LogError("WareHouseExceptions AddElementByDocument.Get ERROR: " + ex.Message);
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError("Exception  AddElementByDocument.Get ERROR: " + ex.Message);
                return BadRequest(ex.Message);
            }
        }
        [HttpPost("/WareHouse/CreateElement/{warehouseid}")]
        public async Task<IActionResult> CreateElementAsync([FromBody] List<MaterialViewModel> materialViewModel, string warehouseid)
        {
            try
            {

                _logger.LogInformation("CreateElement.Post: " + JsonConvert.SerializeObject(materialViewModel));
                int result = await _IWareHouseServices.CreateMaterialWarehouse(materialViewModel, warehouseid);
                _logger.LogInformation($@"CreateElement.Post CORRECTO, NUMERO DE LEGALIZACIÓN : {result}");
                return Ok(NUM_DOCUMENT_ELEMENT + result.ToString());
            }

            catch (WareHouseExceptions ex)
            {
                _logger.LogError("WareHouseExceptions CreateElement.Post ERROR: " + ex.Message);
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError("CreateElement.Post ERROR: " + ex.Message);
                return BadRequest(ex.Message);
            }
        }
        [HttpPost("/WareHouse/CreateElements/{warehouseid}")]
        public async Task<IActionResult> CreateElementsAsync([FromBody] List<List<MaterialViewModel>> materialViewModel, string warehouseid)
        {
            try
            {
                _logger.LogInformation("CreateElements.Post: " + JsonConvert.SerializeObject(materialViewModel));
                List<int> listNumDocumentsresult = new List<int>();
                int result = 0;

                foreach (List<MaterialViewModel> item in materialViewModel)
                {
                    if (item.Count > 0)
                    {
                        result = await _IWareHouseServices.CreateMaterialWarehouse(item, warehouseid);
                        listNumDocumentsresult.Add(result);
                        _logger.LogInformation($@"CreateElements.Post CORRECTO, NUMERO DE LEGALIZACIÓN : {result}");
                    }
                }

                return Ok(listNumDocumentsresult);
            }

            catch (WareHouseExceptions ex)
            {
                _logger.LogError("WareHouseExceptions CreateElements.Post ERROR: " + ex.Message);
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError("Exception CreateElements.Post ERROR: " + ex.Message);
                return BadRequest(ex.Message);
            }
        }


    }
}