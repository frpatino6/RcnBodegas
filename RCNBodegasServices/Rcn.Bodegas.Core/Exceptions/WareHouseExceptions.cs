using System;

namespace Rcn.Bodegas.Core.Exceptions
{
  public class WareHouseExceptions : Exception
  {
    public WareHouseExceptions()
    {
    }

    public WareHouseExceptions(string userName) : base($"No warehouse associated with the user were found { userName}")
    {
    }
    public WareHouseExceptions(string wareHouseName, string message) : base(message)
    {
    }
  }
}
