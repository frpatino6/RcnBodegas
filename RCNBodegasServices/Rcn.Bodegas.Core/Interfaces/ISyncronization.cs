using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;

namespace Rcn.Bodegas.Core.Interfaces
{
    public interface ISyncronization
    {
        List<ProductionViewModel> GetAllListProductions();

        List<ResponsibleViewModel> GetListAllResponsible();

        List<TipoElementoViewModel> GetAllListTipoElemento();

        List<TipoElementoViewModel> GetAllListTipoPrenda();

        List<WareHouseViewModel> GetListAllWarehouseUser();

        Int64 GetCountMaterial(string warehouseType);

        List<MaterialViewModel> GetListAllMaterial(Int64 offSet, string warehouseType);

        List<ResponsibleViewModel> GetAllListWarehouseUser();
    }
}
