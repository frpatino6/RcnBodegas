using System;
using System.Collections.Generic;
using System.Text;

namespace Rcn.Bodegas.Core.ViewModel
{
  public class WareHouseViewModel
  {
    public int Id { get; set; }

    public string WareHouseName { get; set; }

    public int Type{ get; set; }

    public int Class { get; set; }

    public string  Location { get; set; }

  
    /// <summary>
    /// Responsable warehouse
    /// </summary>
    public string RespoWareHouse { get; set; }
  }
}
