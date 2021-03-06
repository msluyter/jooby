package org.jooby.internal.hbm;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jooby.MockUnit;
import org.jooby.Response;
import org.jooby.Result;
import org.jooby.Results;
import org.junit.Test;

public class TrxResponseTest {

  @Test
  public void defaults() throws Exception {
    Result result = Results.ok();
    new MockUnit(Response.class, EntityManager.class)
        .expect(unit -> {
          unit.get(Response.class).send((Object) result);
        })
        .expect(unit -> {
          Session session = unit.mock(Session.class);
          session.flush();
          session.setDefaultReadOnly(true);
          session.setDefaultReadOnly(false);
          session.setFlushMode(FlushMode.MANUAL);

          EntityTransaction trx = unit.mock(EntityTransaction.class);
          expect(trx.isActive()).andReturn(true);
          trx.commit();

          EntityTransaction rotrx = unit.mock(EntityTransaction.class);
          rotrx.begin();
          rotrx.commit();

          EntityManager em = unit.get(EntityManager.class);
          expect(em.getDelegate()).andReturn(session);

          expect(em.getTransaction()).andReturn(trx);
          expect(em.getTransaction()).andReturn(rotrx);
        })
        .run(unit -> {
          new TrxResponse(unit.get(Response.class), unit.get(EntityManager.class))
              .send(result);
        });
  }

  @Test(expected = HibernateException.class)
  public void silentReadOnlyTrx() throws Exception {
    Result result = Results.ok();
    new MockUnit(Response.class, EntityManager.class)
        .expect(unit -> {
          unit.get(Response.class).send((Object) result);
        })
        .expect(unit -> {
          Session session = unit.mock(Session.class);
          session.flush();
          session.setDefaultReadOnly(true);
          session.setDefaultReadOnly(false);
          session.setFlushMode(FlushMode.MANUAL);
          expectLastCall().andThrow(new HibernateException("intentional err"));

          EntityTransaction trx = unit.mock(EntityTransaction.class);
          expect(trx.isActive()).andReturn(true);
          trx.commit();
          expect(trx.isActive()).andReturn(false);

          EntityManager em = unit.get(EntityManager.class);
          expect(em.getDelegate()).andReturn(session);

          expect(em.getTransaction()).andReturn(trx);
        })
        .run(unit -> {
          new TrxResponse(unit.get(Response.class), unit.get(EntityManager.class))
              .send(result);
        });
  }

  @Test(expected = HibernateException.class)
  public void silentReadOnlyTrx2() throws Exception {
    Result result = Results.ok();
    new MockUnit(Response.class, EntityManager.class)
        .expect(unit -> {
          unit.get(Response.class).send((Object) result);
        })
        .expect(unit -> {
          Session session = unit.mock(Session.class);
          session.flush();
          session.setDefaultReadOnly(true);
          session.setDefaultReadOnly(false);
          session.setFlushMode(FlushMode.MANUAL);

          EntityTransaction trx = unit.mock(EntityTransaction.class);
          expect(trx.isActive()).andReturn(true);
          trx.commit();
          expect(trx.isActive()).andReturn(false);

          EntityTransaction rotrx = unit.mock(EntityTransaction.class);
          rotrx.begin();
          expectLastCall().andThrow(new HibernateException("intentional err"));

          expect(rotrx.isActive()).andReturn(true);
          rotrx.rollback();

          EntityManager em = unit.get(EntityManager.class);
          expect(em.getDelegate()).andReturn(session);

          expect(em.getTransaction()).andReturn(trx);
          expect(em.getTransaction()).andReturn(rotrx);
        })
        .run(unit -> {
          new TrxResponse(unit.get(Response.class), unit.get(EntityManager.class))
              .send(result);
        });
  }

  @Test(expected = HibernateException.class)
  public void silentReadOnlyTrx3() throws Exception {
    Result result = Results.ok();
    new MockUnit(Response.class, EntityManager.class)
        .expect(unit -> {
          unit.get(Response.class).send((Object) result);
        })
        .expect(unit -> {
          Session session = unit.mock(Session.class);
          session.flush();
          session.setDefaultReadOnly(true);
          session.setDefaultReadOnly(false);
          session.setFlushMode(FlushMode.MANUAL);

          EntityTransaction trx = unit.mock(EntityTransaction.class);
          expect(trx.isActive()).andReturn(true);
          trx.commit();
          expect(trx.isActive()).andReturn(false);

          EntityTransaction rotrx = unit.mock(EntityTransaction.class);
          rotrx.begin();
          expectLastCall().andThrow(new HibernateException("intentional err"));

          expect(rotrx.isActive()).andReturn(false);

          EntityManager em = unit.get(EntityManager.class);
          expect(em.getDelegate()).andReturn(session);

          expect(em.getTransaction()).andReturn(trx);
          expect(em.getTransaction()).andReturn(rotrx);
        })
        .run(unit -> {
          new TrxResponse(unit.get(Response.class), unit.get(EntityManager.class))
              .send(result);
        });
  }

  @Test
  public void defaultsNoActiveTrx() throws Exception {
    Result result = Results.ok();
    new MockUnit(Response.class, EntityManager.class)
        .expect(unit -> {
          unit.get(Response.class).send((Object) result);
        })
        .expect(unit -> {
          Session session = unit.mock(Session.class);
          session.setDefaultReadOnly(true);
          session.setDefaultReadOnly(false);
          session.setFlushMode(FlushMode.MANUAL);
          session.flush();

          EntityTransaction trx = unit.mock(EntityTransaction.class);
          expect(trx.isActive()).andReturn(false);

          EntityTransaction rotrx = unit.mock(EntityTransaction.class);
          rotrx.begin();
          rotrx.commit();

          EntityManager em = unit.get(EntityManager.class);
          expect(em.getDelegate()).andReturn(session);

          expect(em.getTransaction()).andReturn(trx);
          expect(em.getTransaction()).andReturn(rotrx);
        })
        .run(unit -> {
          new TrxResponse(unit.get(Response.class), unit.get(EntityManager.class))
              .send(result);
        });
  }

  @Test(expected = HibernateException.class)
  public void defaultsRollbackActiveTrx() throws Exception {
    Result result = Results.ok();
    new MockUnit(Response.class, EntityManager.class)
        .expect(unit -> {
          unit.get(Response.class).send((Object) result);
        })
        .expect(unit -> {
          Session session = unit.mock(Session.class);
          session.flush();

          EntityTransaction trx = unit.mock(EntityTransaction.class);
          expect(trx.isActive()).andThrow(new HibernateException("intentional err"));
          expect(trx.isActive()).andReturn(true);
          trx.rollback();

          EntityTransaction rotrx = unit.mock(EntityTransaction.class);
          rotrx.begin();
          rotrx.commit();

          EntityManager em = unit.get(EntityManager.class);
          expect(em.getDelegate()).andReturn(session);

          expect(em.getTransaction()).andReturn(trx);
          expect(em.getTransaction()).andReturn(rotrx);
        })
        .run(unit -> {
          new TrxResponse(unit.get(Response.class), unit.get(EntityManager.class))
              .send(result);
        });
  }

}
