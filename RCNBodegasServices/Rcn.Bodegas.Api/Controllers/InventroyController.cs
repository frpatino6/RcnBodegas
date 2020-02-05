using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Api.Controllers
{
    [Produces("application/json")]
    public class InventroyController : Controller
    {
        private readonly ILogger<InventroyController> _logger;
        private readonly IInventroyService _IInventroy;
        private readonly IHostingEnvironment _hostingEnvironment;
        public InventroyController(IInventroyService inventroy, ILogger<InventroyController> logger, IHostingEnvironment hostingEnvironment)
        {
            _IInventroy = inventroy;
            _logger = logger;
            _hostingEnvironment = hostingEnvironment;

            if (_hostingEnvironment.IsProduction())
            {
                _logger.LogInformation("Producción");
            }
            else
            {
                _logger.LogInformation("Desarrollo");
            }

        }

        [HttpGet("/Inventory/GetListProduction/{warehouseid=0}")]
        public async Task<IActionResult> GetListProductionAsync(string warehouseid)
        {
            try
            {
                System.Collections.Generic.List<Core.ViewModel.ProductionViewModel> result = await _IInventroy.GetListProductions(warehouseid);
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

        [HttpGet("/Inventory/GetListResponsable/{warehouse=''}/{production=''}")]
        public async Task<IActionResult> GetListResponsableAsync(string warehouse, string production)
        {
            try
            {
                _logger.LogInformation("Iniciando Get GetListResponsable {warehouse}{production} ");
                System.Collections.Generic.List<Core.ViewModel.ResponsibleViewModel> result = await _IInventroy.GetListResponsible(warehouse, production);
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

        [HttpGet("/Inventory/GetListWarehouseUser/{tipoBodega=''}")]
        public async Task<IActionResult> GetListWarehouseUserAsync(string tipoBodega)
        {
            try
            {
                System.Collections.Generic.List<Core.ViewModel.ResponsibleViewModel> result = await _IInventroy.GetListWarehouseUserAsync(tipoBodega);
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
                System.Collections.Generic.List<Core.ViewModel.TipoElementoViewModel> result = await _IInventroy.GetListTipoElemento(warehouseid);
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
                Core.ViewModel.MaterialViewModel result = await _IInventroy.GetMaterialByBarCode(barcode);
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

        [HttpGet("/Inventory/GetMaterialByProduction/{wareHouseType}/{production}/{responsible}/{type_element}/{continueInventory}/{inventoryId}/{fechaMovimiento}")]
        public async Task<IActionResult> GetMaterialByProduction(string wareHouseType, int production, int responsible, int type_element, int continueInventory, int inventoryId, string fechaMovimiento)
        {
            try
            {
                System.Collections.Generic.List<Core.ViewModel.MaterialViewModel> result = _IInventroy.GetMaterialsForProduction(wareHouseType, production, responsible, type_element, continueInventory, inventoryId, fechaMovimiento);
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

        [HttpGet("/Inventory/GetCountMaterialByProduction/{wareHouseType}/{production}/{responsible}/{type_element}")]
        public async Task<IActionResult> GetCountMaterialByProduction(string wareHouseType, int production, int responsible, int type_element)
        {
            try
            {
                Int64 result = await _IInventroy.GetMaterialsCountForProduction(wareHouseType, production, responsible, type_element);
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
        [HttpPost("/Inventory/CreateInventoryHeader/")]
        public async Task<IActionResult> CreateInventoryHeader([FromBody] InvetoryHeaderViewModel invetoryViewModel)
        {
            try
            {
                int result = _IInventroy.CreateInventoryHeader(invetoryViewModel);
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
        [HttpPost("/Inventory/CreateInventoryDetail/{status}/{inventoryId}")]
        public async Task<IActionResult> CreateInventoryDetail([FromBody] List<InventoryDetailViewModel> inventoryDetailViewModel, int status, int inventoryId)
        {
            try
            {
                foreach (InventoryDetailViewModel item in inventoryDetailViewModel)
                {
                    _IInventroy.CreateInventoryDetail(item, status);
                }
                if (inventoryDetailViewModel.Count == 0)
                {
                    _IInventroy.UpdateStateInventory(inventoryId, status);
                }
                return Ok();
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

        [HttpGet("/Inventory/GetPendingInventoryByUser/{user}")]
        public async Task<IActionResult> GetPendingInventoryByUser(string user)
        {
            try
            {
                List<InvetoryHeaderViewModel> result = await _IInventroy.GetPendingInventoryByUser(user);

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

        [HttpGet("/Inventory/GetPendingInventoryById/{id}")]
        public async Task<IActionResult> GetPendingInventoryById(int id)
        {
            try
            {
                List<InvetoryHeaderViewModel> result = await _IInventroy.GetPendingInventoryById(id);

                if (result != null)
                {
                    return Ok(result);
                }
                else
                {
                    return NotFound($@"No se pudo recuperar elementos para el código de inventario {id}");
                }
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

        [HttpGet("/Inventory/GetPendingInventory")]
        public async Task<IActionResult> GetPendingInventory()
        {
            try
            {
                List<InvetoryHeaderViewModel> result = await _IInventroy.GetPendingInventory();

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