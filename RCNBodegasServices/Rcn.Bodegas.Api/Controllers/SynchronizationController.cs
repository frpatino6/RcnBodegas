using Microsoft.AspNetCore.Mvc;
using Rcn.Bodegas.Core.Exceptions;
using Rcn.Bodegas.Core.Interfaces;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Api.Controllers
{
    [Produces("application/json")]
    public class SynchronizationController : Controller
    {
        private readonly ISyncronization _ISyncronization;

        public SynchronizationController(ISyncronization syncronization)
        {
            _ISyncronization = syncronization;
        }
        [HttpGet("/sync/GetListAllWarehouse/")]
        public async Task<IActionResult> GetListAllWarehouse()
        {
            try
            {
                List<Core.ViewModel.WareHouseViewModel> result = _ISyncronization.GetListAllWarehouseUser();
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
                List<Core.ViewModel.ProductionViewModel> result = _ISyncronization.GetAllListProductions();
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
                List<Core.ViewModel.TipoElementoViewModel> result = _ISyncronization.GetAllListTipoElemento();
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
                List<Core.ViewModel.TipoElementoViewModel> result = _ISyncronization.GetAllListTipoPrenda();
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
                List<Core.ViewModel.ResponsibleViewModel> result = _ISyncronization.GetListAllResponsible();
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

        [HttpGet("/sync/GetListAllMaterial/{OffSet}/{warehouseType}")]
        public async Task<IActionResult> GetListAllMaterialAsync(Int64 OffSet, string warehouseType)
        {
            try
            {
                List<Core.ViewModel.MaterialViewModel> result = _ISyncronization.GetListAllMaterial(OffSet, warehouseType);
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
                List<Core.ViewModel.ResponsibleViewModel> result = _ISyncronization.GetAllListWarehouseUser();
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

        [HttpGet("/sync/GetCountMateriales/{warehouseType}")]
        public async Task<IActionResult> GetCountMaterialesAsync(string warehouseType)
        {
            try
            {
                long result = _ISyncronization.GetCountMaterial(warehouseType);
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
