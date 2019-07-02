using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Interfaces
{
  public interface IInventroyService
  {
    Task<List<ProductionViewModel>> GetListProductions(string wareHouse);

    Task<List<ResponsibleViewModel>> GetListResponsible(string production);

    Task<List<TipoElementoViewModel>> GetListTipoElemento();
  }
}
