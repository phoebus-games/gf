package gf.model.roulette

import gf.model.core.SimpleWallet
import org.junit.Assert._
import org.junit.Test

class RouletteTest {
  private val wallet = SimpleWallet()
  private val wager = BigDecimal(10)
  //noinspection ForwardReference
  private val random = () => winningPocket
  private val roulette = Roulette(random = random, wallet = wallet)
  private var winningPocket = Pocket(1)
  private var losingPocket = Pocket(20)

  @Test def pocketCanBeZero(): Unit = Pocket(0)

  @Test def pocketCanBe36(): Unit = Pocket(36)

  @Test def pocket0IsNotRed(): Unit = assertFalse(Pocket(0).isRed)

  @Test def pocket19IsRed(): Unit = assertTrue(Pocket(19).isRed)

  @Test def pocket33IsNotRed(): Unit = assertFalse(Pocket(33).isRed)

  @Test def pocket0IsNotBlack(): Unit = assertFalse(Pocket(0).isBlack)

  @Test def pocket19IsNotRed(): Unit = assertFalse(Pocket(19).isBlack)

  @Test def pocket33IsBlack(): Unit = assertTrue(Pocket(33).isBlack)

  @Test(expected = classOf[IllegalArgumentException]) def pocketCannotBeNegative(): Unit =
    Pocket(-1)

  @Test(expected = classOf[IllegalArgumentException]) def pocketCannotBe37(): Unit =
    Pocket(37)

  @Test(expected = classOf[IllegalArgumentException]) def cannotBetOnZero(): Unit = {
    roulette.addBet(NumberBet(wager, Pocket(0)))
  }

  @Test(expected = classOf[IllegalArgumentException]) def cannotBetOn37(): Unit = {
    roulette.addBet(NumberBet(wager, Pocket(37)))
  }

  @Test(expected = classOf[IllegalArgumentException]) def wagerAmountCannotBetNegative(): Unit = {
    roulette.addBet(NumberBet(BigDecimal(-1), winningPocket))
  }

  @Test def addingBetDecreasesBalance(): Unit = {
    roulette.addBet(winningBet())
    assertEquals(BigDecimal(990), wallet.getBalance)
  }

  @Test def addedBetIsAdded(): Unit = {
    val bet = NumberBet(wager, Pocket(1))

    assertEquals(List(bet), roulette.addBet(bet).bets)
  }

  @Test def spinClearsBets(): Unit = {
    assertEquals(List.empty, roulette.addBet(winningBet())
      .spin().bets)
  }

  private def winningBet() = NumberBet(wager, winningPocket)

  @Test def spinEmptiesBet(): Unit = {
    assertEquals(List.empty, roulette.addBet(winningBet())
      .spin().bets)
  }

  @Test def losingBetDoesNotPayout(): Unit = {
    roulette.addBet(losingBet())
      .spin()
    assertEquals(BigDecimal(990), wallet.getBalance)
  }

  private def losingBet() = NumberBet(wager, losingPocket)

  @Test def winningNumberBetPays36x() = {
    roulette.addBet(winningBet())
      .spin()
    assertEquals(BigDecimal(990 + 360), wallet.getBalance)
  }

  @Test def winningRedBetPaysEvens() = {
    roulette.addBet(RedBet(wager))
      .spin()
    assertEquals(BigDecimal(1010), wallet.getBalance)
  }

  @Test def losingRedBetPaysZero() = {
    winningPocket = Pocket(20)
    roulette.addBet(RedBet(wager))
      .spin()
    assertEquals(BigDecimal(990), wallet.getBalance)
  }

  @Test def winningBlackBetPaysEvens() = {
    winningPocket = Pocket(33)
    roulette.addBet(BlackBet(wager))
      .spin()
    assertEquals(BigDecimal(1010), wallet.getBalance)
  }

  @Test def losingBlackBetPaysZero() = {
    roulette.addBet(BlackBet(wager))
      .spin()
    assertEquals(BigDecimal(990), wallet.getBalance)
  }
}